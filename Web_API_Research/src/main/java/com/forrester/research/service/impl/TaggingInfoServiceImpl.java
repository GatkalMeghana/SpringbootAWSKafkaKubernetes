package com.forrester.research.service.impl;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.ContentfulClient;
import com.forrester.research.clients.contentful.response.ContentfulModelStore;
import com.forrester.research.clients.contentful.response.models.Research;
import com.forrester.research.clients.permissions.response.ResearchPermission;
import com.forrester.research.clients.taxonomy.TaxonomyClient;
import com.forrester.research.clients.taxonomy.response.TaxonomyTag;
import com.forrester.research.clients.taxonomy.response.TaxonomyTags;
import com.forrester.research.entity.*;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.repo.WebProductFamilyRepository;
import com.forrester.research.service.TaggingInfoService;
import com.forrester.research.utils.IPTypeEnum;
import com.forrester.research.utils.TaggingInfoTypeEnum;
import com.forrester.research.utils.TaxonomyUtils;
import com.forrester.research.utils.UserPermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.forrester.research.utils.TaxonomyUtils.isISGData;

@Service
public class TaggingInfoServiceImpl implements TaggingInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaggingInfoServiceImpl.class);

    @Autowired
    private TaxonomyClient taxonomyClient;

    @Autowired
    private WebProductFamilyRepository webProductFamilyRepository;

    @Autowired
    private ContentfulModelStore contentfulModelStore;

    @Autowired
    private ContentfulClient contentfulClient;

    @Autowired
    private UserPermissionUtil permissionsUtil;

    private static final String CHILDREN = "children";

    private Map<String, Integer> serviceToIdMapping;

    @Value("${forr.taxonomy.isg.id}")
    private String isgId;

    @Value("${forr.services.mapping}")
    private String serviceMappingConfig;

    @Value("#{'${forr.tags.aocti.not.access}'.split(',', -1)}")
    private List<String> aocTiNotAccessTagIds;

    @Value("#{'${forr.services.mi.has.access}'.split(',', -1)}")
    private List<String> servicesFMIHasAccess;

    @Value("#{${forr.roles.mapping}}")
    private Map<String, List<String>> roleMap;

    @Value("${forr.filter.fmi}")
    private List<String> filterFmi;

    @PostConstruct
    public void init() {
        serviceToIdMapping = Arrays.stream(serviceMappingConfig.trim().split(",", -1))
                .collect(Collectors.toMap(key -> key.split("\\|")[0], value -> Integer.parseInt(value.split("\\|")[1]), (k1, k2) -> k1, TreeMap::new));
    }

    @Override
    public AccessInfo getClientAccess(String contentIds,  String userEmail) throws DataNotFoundException, AuthorizationException {
        List<ResearchPermission> researchPermissionList = permissionsUtil.getPermissionsByContentIdAndUserEmail(userEmail,
                contentIds);

        return new AccessInfo(researchPermissionList.get(0).isHasAccess());
    }

    @Override
    public TaggingInfo getTaggingInfo(String contentId) {
        TaxonomyTags taxonomyTags = taxonomyClient.getTags(contentId);
        TaggingInfo taggingInfo = new TaggingInfo();
        List<Category> categories = new ArrayList<>();

        // Checks if the report has ISG Data access.
        taggingInfo.setIsISGData(isISGData(taxonomyTags, isgId));

        IPTypeEnum ipType = getIpTypeByContentId(contentId);

        // Bold Vision
        List<List<TagDto>> visionRows = getBoldVisionRows(taxonomyTags);

        // Forrester Decisions
        boolean isAddVisionTags = getForresterDecisionsProducts(taxonomyTags, categories, taggingInfo, visionRows, ipType);

        // MI
        getMIProducts(taxonomyTags, categories, taggingInfo, !visionRows.isEmpty(), ipType);


        // Heritage Forrester
        getOldForresterProducts(taxonomyTags, categories, taggingInfo, isAddVisionTags);

        // Heritage Sirius Decisions
        getSdProducts(taxonomyTags, categories, taggingInfo);

        return taggingInfo;
    }

    private boolean getForresterDecisionsProducts(TaxonomyTags taxonomyTags, List<Category> categories,
                                                  TaggingInfo taggingInfo,
                                                  List<List<TagDto>> visionRows,
                                                  IPTypeEnum ipType) {
        Category fdCategory = new Category();
        fdCategory.setTitle(Constants.FORRESTER_DECISIONS);

        List<String> servicesHeaders = new ArrayList<>();
        servicesHeaders.add(Constants.SERVICE);
        servicesHeaders.add(Constants.PRIORITY);
        servicesHeaders.add(Constants.PHASE);
        Item servicesItem = new Item();
        servicesItem.setHeaders(servicesHeaders);

        List<String> visionHeaders = new ArrayList<>();
        visionHeaders.add(Constants.VISION);
        Item visionItem = new Item();
        visionItem.setHeaders(visionHeaders);

        // Checks that the report has Group Reader Access
        taggingInfo.setIsGroupReaderAccess(!taggingInfo.getIsISGData() && (!CollectionUtils.isEmpty(visionRows) || isTaggedAtServiceLevel(taxonomyTags, Boolean.TRUE)));

        List<List<TagDto>> allRows = getRows(taxonomyTags, TaggingInfoTypeEnum.TYPE_SERVICES, Boolean.FALSE, Boolean.FALSE, null, Boolean.FALSE);
        List<List<TagDto>> filteredRows = filterProductsRows(allRows, Boolean.FALSE);

        servicesItem.setRows(filteredRows);

        visionItem.setRows(visionRows);

        List<Item> fdItems = new ArrayList<>();
        fdItems.add(servicesItem);
        fdItems.add(visionItem);

        fdCategory.setItems(fdItems);
        categories.add(fdCategory);

        taggingInfo.setCategories(categories);

        if (ipType != null) {
            if (!IPTypeEnum.DATA_SNAPSHOT.equals(ipType) && !IPTypeEnum.DATA_OVERVIEW.equals(ipType)) {
                return !CollectionUtils.isEmpty(visionRows);
            }
            return !checkAoCTiAccessTags(taxonomyTags, Boolean.FALSE, TaggingInfoTypeEnum.TYPE_VISION);
        }

        return false;
    }

    private IPTypeEnum getIpTypeByContentId(String contentId) {
        IPTypeEnum ipType = null;

        try {
            Class<?> contentModel = contentfulModelStore.getModel(Constants.RESEARCH);
            List<Object> researchObjects = contentfulClient.getEntryDetailsByEntryIds(contentModel, new String[]{contentId});
            if (!CollectionUtils.isEmpty(researchObjects)) {
                Research research = (Research) researchObjects.get(0);
                ipType = IPTypeEnum.findBy(research.getIpType());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }

        return ipType;
    }

    private boolean isTaggedToDefinePhase(TaggingInfo taggingInfo) {
        Category fdCategory = taggingInfo.getCategories().get(0);
        if (fdCategory != null) {
            Item fdItem = fdCategory.getItems().get(0);
            List<List<TagDto>> rows = fdItem.getRows();
            return rows.stream().anyMatch(row ->
                    row.size() == 3 && row.get(2) != null &&
                            row.get(2).getTagName().equalsIgnoreCase(Constants.PHASE_DEFINE));
        }
        return false;
    }

    private void getMIProducts(TaxonomyTags taxonomyTags, List<Category> categories, TaggingInfo taggingInfo,
                               boolean hasVisionRows, IPTypeEnum ipType) {

        Category fmiCategory = new Category();
        fmiCategory.setTitle(Constants.MARKET_INSIGHTS);

        List<String> servicesHeaders = new ArrayList<>();
        servicesHeaders.add(Constants.MARKET);
        Item servicesItem = new Item();
        servicesItem.setHeaders(servicesHeaders);

        List<List<TagDto>> allRows;
        List<List<TagDto>> filteredRows;

        if(hasVisionRows || IPTypeEnum.WAVE.equals(ipType) || isTaggedToDefinePhase(taggingInfo) ||
                isTaggedAtServiceLevel(taxonomyTags, Boolean.FALSE)){
            filteredRows = addFmiServicesNames();
        } else{
            allRows= getRows(taxonomyTags, TaggingInfoTypeEnum.TYPE_SERVICES, Boolean.FALSE, Boolean.TRUE, null, Boolean.FALSE);
            filteredRows = filterProductsRows(allRows, Boolean.TRUE);
        }

        servicesItem.setRows(filteredRows);

        List<Item> fdItems = new ArrayList<>();
        fdItems.add(servicesItem);

        fmiCategory.setItems(fdItems);
        categories.add(fmiCategory);

        taggingInfo.setCategories(categories);
    }

    private void getOldForresterProducts(TaxonomyTags taxonomyTags, List<Category> categories, TaggingInfo taggingInfo, boolean isAddVisionTags) {
        Category heritageAocCategory = new Category();
        heritageAocCategory.setTitle(Constants.HERITAGE_FORRESTER);

        List<String> productHeaders = new ArrayList<>();
        productHeaders.add(Constants.PRODUCTS);
        Item productsItem = new Item();
        productsItem.setHeaders(productHeaders);

        List<List<TagDto>> rows = new ArrayList<>();
        List<List<TagDto>> oldProductsRows = getOldProductsRows(taxonomyTags);
        List<List<TagDto>> oldRolesRows = getRows(taxonomyTags, TaggingInfoTypeEnum.TYPE_OLD_ROLE, Boolean.TRUE, Boolean.TRUE, oldProductsRows, isAddVisionTags);
        rows.addAll(oldProductsRows);
        rows.addAll(oldRolesRows);

        // All Heritage Forrester Products get ordered alphabetically.
        List<List<TagDto>> orderedRows = rows.stream().sorted(Comparator.comparing(list -> list.get(0).getTagName())).collect(Collectors.toList());

        productsItem.setRows(orderedRows);

        List<Item> heritageAocItems = new ArrayList<>();
        heritageAocItems.add(productsItem);

        heritageAocCategory.setItems(heritageAocItems);
        categories.add(heritageAocCategory);

        taggingInfo.setCategories(categories);
    }

    private void getSdProducts(TaxonomyTags taxonomyTags, List<Category> categories, TaggingInfo taggingInfo) {
        Category heritageSd = new Category();
        heritageSd.setTitle(Constants.HERITAGE_SIRIUSDECISIONS);

        Item heritageSdItem = new Item();
        heritageSdItem.setHeaders(Arrays.asList(Constants.SERVICE, Constants.PRIORITY));

        heritageSdItem.setRows(getRows(taxonomyTags, TaggingInfoTypeEnum.TYPE_SD_SERVICES, Boolean.TRUE, Boolean.FALSE, null, Boolean.FALSE));

        heritageSd.setItems(Collections.singletonList(heritageSdItem));
        categories.add(heritageSd);

        taggingInfo.setCategories(categories);
    }

    private boolean isMiOrMiEgService(TaxonomyTag tag, String service) {
        return checkForTags(getServiceIdFromTag(tag), service);
    }

    private Integer getServiceIdFromTag(TaxonomyTag tag) {
        String serviceId;
        if (tag.isTaggedAtParentLevel()) {
            serviceId = tag.getId();
        } else if (tag.isTaggedAtChildLevel()) {
            serviceId = tag.getParent().getId();
        } else {
            serviceId = tag.getParent().getParent().getId();
        }
        return Integer.valueOf(serviceId);
    }

    private boolean checkForTags(Integer serviceTagId, String service) {
        return serviceTagId.equals(serviceToIdMapping.get(service));
    }

    private List<List<TagDto>> getRows(TaxonomyTags taxonomyTags,
                                       TaggingInfoTypeEnum taggingInfoTypeEnum,
                                       boolean isLegacy,
                                       boolean onlyGrandParent,
                                       List<List<TagDto>> oldProductsRows,
                                       boolean isAddVisionTags) {
        Map<String, Tag> tagsMap = new ConcurrentHashMap<>();
        List<List<TagDto>> rows = new ArrayList<>();

        String tagId = isLegacy ? taggingInfoTypeEnum.getTypeId() : null;

        /*
         * The following code filter the tags related to the section we are evaluating (FD Services, vision/Themes, SD Services or Old Forrester Products)
         * */
        List<TaxonomyTag> tags = taxonomyTags.getTags()
                .stream()
                .filter(taxonomyTag ->
                        taggingInfoTypeEnum.equals(taxonomyTag.getType(isLegacy, tagId)))
                .collect(Collectors.toList());

        tags.forEach(tag -> {
            if (tag.isTaggedAtParentLevel()) {
                if (!tagsMap.containsKey(tag.getName())) {
                    tagsMap.put(tag.getName(), new Tag(tag.getName()));
                }
            } else {
                /*
                 * This conditional is to know if the tag is a parent.
                 * */
                if (tag.isTaggedAtChildLevel()) {
                    String parentName = tag.getParent().getName();

                    /*
                     * This conditional is to know if the grandparent is already in the map. If not, it is added to the map, and it means it isn´t tagged to the document.
                     * */
                    if (!tagsMap.containsKey(parentName)) {
                        tagsMap.put(parentName, new Tag(parentName));
                    }
                    Tag granParent = tagsMap.get(parentName);

                    /*
                     * This conditional is to know if the parent is already in the map. If not, it is added to the map, and it means it is tagged to the document.
                     * */
                    if (!granParent.getChildren().containsKey(tag.getName())) {
                        List<Tag> tagList = new ArrayList<>();
                        tagList.add(new Tag(tag.getName()));
                        granParent.getChildren().put(tag.getName(), tagList);
                    }

                    tagsMap.put(parentName, granParent);
                } else {
                    /*
                     * This means the tag is a child.
                     * */
                    String parent = tag.getParent().getName();
                    String granParentName = tag.getParent().getParent().getName();

                    /*
                     * This conditional is to know if the grandparent is already in the map. If not, it is added to the map, and it means it isn´t tagged to the document.
                     * */
                    if (!tagsMap.containsKey(granParentName)) {
                        tagsMap.put(granParentName, new Tag(granParentName));
                    }
                    Tag granParent = tagsMap.get(granParentName);

                    /*
                     * This conditional is to know if the parent is already in the map. If not, it is added to the map, and it means it isn´t tagged to the document.
                     * */
                    if (!granParent.getChildren().containsKey(parent)) {
                        List<Tag> parentList = new ArrayList<>();
                        List<Tag> childList = new ArrayList<>();
                        parentList.add(new Tag(parent));
                        childList.add(new Tag(tag.getName()));
                        granParent.getChildren().put(parent, parentList);

                        /*
                         * The child is added to the map, and it means it is tagged to the document.
                         * */
                        granParent.getChildren().get(parent).get(0).getChildren().put(CHILDREN, childList);
                    } else {
                        /*
                         * This conditional is to know if the parent is already has any child. If not, the children array is initialized with an empty one.
                         * */
                        if (!granParent.getChildren().get(parent).get(0).getChildren().containsKey(CHILDREN)) {
                            granParent.getChildren().get(parent).get(0).getChildren().put(CHILDREN, new ArrayList<>());
                        }

                        /*
                         * The child is added to the map, and it means it is tagged to the document.
                         * */
                        granParent.getChildren().get(parent).get(0).getChildren().get(CHILDREN).add(new Tag(tag.getName()));

                    }

                    /*
                     * Finally, the grandparent data is updated in the map
                     * */
                    tagsMap.put(granParentName, granParent);
                }
            }
        });

        /*
         * Here the logic goes through the map and create the expected response´s structure.
         * [
         *  {"tagName": "parentName", "active": true},
         *  {"tagName": "childName ", "active": false},
         *  {"tagName": "grandchildName ", "active": true},
         * ]
         * */
        if (isAddVisionTags) {
            addHeritageForresterByVision(rows, oldProductsRows);
        }

        tagsMap.forEach((key, granParent) -> {
            if (granParent.getChildren().size() > 0 && !onlyGrandParent) {
                granParent.getChildren().forEach((parentName, parents) -> parents.forEach(parent -> {
                    if (parent.getChildren().size() > 0) {
                        parent.getChildren().forEach((childName, children) -> children.forEach(child ->
                                addFdRowFormat(rows, granParent, parent, child)
                        ));
                    } else {
                        addFdRowFormat(rows, granParent, parent, null);
                    }
                }));
            } else {
                if (onlyGrandParent) {
                    List<String> tagNames = roleMap.get(granParent.getTagName());
                    if (!CollectionUtils.isEmpty(tagNames)) {
                        for (String tagName : tagNames) {
                            boolean tagNameAdded = false;
                            for (List<TagDto> addedRow : rows) {
                                if (addedRow.get(0).getTagName().equalsIgnoreCase(tagName)) {
                                    tagNameAdded = true;
                                    break;
                                }
                            }
                            if (!tagNameAdded) {
                                for (List<TagDto> addedRow : oldProductsRows) {
                                    if (addedRow.get(0).getTagName().equalsIgnoreCase(tagName)) {
                                        tagNameAdded = true;
                                        break;
                                    }
                                }
                            }

                            if (!tagNameAdded) {
                                rows.add(Collections.singletonList(new TagDto(tagName)));
                            }
                        }
                    } else {
                        rows.add(Collections.singletonList(new TagDto(granParent.getTagName())));
                    }
                }
                if (!onlyGrandParent) {
                    addFdRowFormat(rows, granParent, null, null);
                }
            }
        });

        return rows.stream()
                .sorted(Comparator.comparing(list -> list.get(0).getTagName()))
                .collect(Collectors.toList());
    }

    private boolean checkAoCTiAccessTags(TaxonomyTags taxonomyTags, boolean isLegacy, TaggingInfoTypeEnum taggingInfoTypeEnum) {
        String categoryTagId = isLegacy ? taggingInfoTypeEnum.getTypeId() : null;

        List<TaxonomyTag> tags = taxonomyTags.getTags()
                .stream()
                .filter(taxonomyTag ->
                        taggingInfoTypeEnum.equals(taxonomyTag.getType(isLegacy, categoryTagId)))
                .collect(Collectors.toList());

        return !CollectionUtils.isEmpty(tags.stream().filter(tag -> {
            String tagId = tag.isTaggedAtParentLevel() ? tag.getId()
                    : tag.isTaggedAtChildLevel() ? tag.getParent().getId()
                    : tag.getParent().getParent().getId();

            return aocTiNotAccessTagIds.contains(tagId);
        }).collect(Collectors.toList()));
    }

    private void addFdRowFormat(List<List<TagDto>> rows, Tag granParent, Tag parent, Tag child) {
        List<TagDto> row = new ArrayList<>();
        row.add(new TagDto(granParent.getTagName()));
        row.add(parent != null ? new TagDto(parent.getTagName()) : null);
        row.add(child != null ? new TagDto(child.getTagName()) : null);
        rows.add(row);
    }

    private void addHeritageForresterByVision(List<List<TagDto>> rows, List<List<TagDto>> oldProductsRows) {
        List<String> tagNames = roleMap.get(Constants.VISION);
        if (!CollectionUtils.isEmpty(tagNames)) {
            for (String tagName : tagNames) {
                boolean tagNameAdded = false;
                for (List<TagDto> addedRow : oldProductsRows) {
                    if (addedRow.get(0).getTagName().equalsIgnoreCase(tagName)) {
                        tagNameAdded = true;
                        break;
                    }
                }
                if (!tagNameAdded) {
                    rows.add(Collections.singletonList(new TagDto(tagName)));
                }
            }
        }
    }

    //for bold vision rows, add fmi service list names
    private List<List<TagDto>> addFmiServicesNames() {
        return filterFmi.stream().map(fmiServiceName -> Collections.singletonList(new TagDto(fmiServiceName))).collect(Collectors.toList());
    }

    /**
     * Checks if a report has a tag that grants Group Reader Access
     * @param taxonomyTags TaxonomyTags
     * @return Boolean
     */
    private Boolean isTaggedAtServiceLevel(TaxonomyTags taxonomyTags, boolean considerAllTags) {
        return taxonomyTags.getTags()
                .stream()
                .filter(taxonomyTag -> TaggingInfoTypeEnum.TYPE_SERVICES
                        .equals(taxonomyTag.getType(false, null)))
                .collect(Collectors.toList())
                .stream()
                .anyMatch(tag -> tag.isTaggedAtParentLevel() && (considerAllTags || !filterFmi.contains(tag.getName())));
    }

    // Allows to return or exclude FMI products
    private List<List<TagDto>> filterProductsRows(List<List<TagDto>> rows, Boolean miProducts) {
            return rows.stream()
                    .filter(miProducts? list -> filterFmi.contains(list.get(0).getTagName()) :
                            list -> !filterFmi.contains(list.get(0).getTagName()))
                    .sorted(Comparator.comparing(list -> list.get(0).getTagName()))
                    .collect(Collectors.toList());
        }

    private List<List<TagDto>> getOldProductsRows(TaxonomyTags taxonomyTags) {
        Set<String> webProductFamilyNames = new HashSet<>();

        List<TaxonomyTag> tags = taxonomyTags.getTags()
                .stream()
                .filter(taxonomyTag ->
                        TaggingInfoTypeEnum.TYPE_OLD_FORRESTER_PRODUCTS.equals(taxonomyTag.getType(Boolean.TRUE, TaggingInfoTypeEnum.TYPE_OLD_FORRESTER_PRODUCTS.getTypeId())))
                .collect(Collectors.toList());

        List<Integer> tagIds = tags.stream().map(taxonomyTag -> TaxonomyUtils.decodeProductTag(taxonomyTag.getReferenceSourceId())).filter(Objects::nonNull).collect(Collectors.toList());

        Set<WebProductFamily> webProductFamilies = webProductFamilyRepository.findWebProductFamiliesById(tagIds);

        Map<Integer, WebProductFamily> webProductFamilyMap = webProductFamilies.stream().collect(Collectors.toMap(WebProductFamily::getWebProductFamilyId, webProductFamily -> webProductFamily));

        for (WebProductFamily webProductFamily : webProductFamilies) {
            WebProductFamily parent = webProductFamilyMap.get(webProductFamily.getParentId());
            if (parent != null) {
                webProductFamily.setParent(parent);
            }
        }

        for (WebProductFamily webProductFamily : webProductFamilies) {
            Boolean isUserDisplay = Boolean.FALSE;
            WebProductFamily node = webProductFamily;
            while (node != null && !isUserDisplay) {
                if (Boolean.TRUE.equals(node.getIsUserDisplay())) {
                    isUserDisplay = Boolean.TRUE;
                    webProductFamilyNames.add(node.getWebProductFamilyName());
                }
                node = node.getParent();
            }
        }

        return webProductFamilyNames.stream()
                .map(webProductFamilyName -> Collections.singletonList(new TagDto(webProductFamilyName)))
                .collect(Collectors.toList());
    }

    /**
     * Filters the tags related to bold vision and create the expected response´s structure
     * [
     *      {"tagName": "childName", "active": true}
     * ]
     * @param taxonomyTags TaxonomyTags
     * @return List of <List<TagDto>>
     * */
    private List<List<TagDto>> getBoldVisionRows(TaxonomyTags taxonomyTags) {
        return getRows(taxonomyTags, TaggingInfoTypeEnum.TYPE_VISION, Boolean.FALSE, Boolean.FALSE, null, Boolean.FALSE).stream()
                .map(row -> Collections.singletonList(new TagDto(
                        row.stream()
                                .filter(tag -> tag != null && StringUtils.isNotBlank(tag.getTagName()))
                                .map(TagDto::getTagName)
                                .collect(Collectors.joining(", ")))))
                .collect(Collectors.toList());
    }
}
