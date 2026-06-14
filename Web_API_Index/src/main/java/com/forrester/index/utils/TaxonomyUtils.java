package com.forrester.index.utils;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.forrester.index.clients.taxonomy.response.Tag;
import com.forrester.index.clients.taxonomy.response.TaxonomyResponse;
import com.forrester.index.elasticsearch.data.taxonomy.PhaseTag;
import com.forrester.index.elasticsearch.data.taxonomy.PriorityTag;
import com.forrester.index.elasticsearch.data.taxonomy.ServiceTag;

public class TaxonomyUtils {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyUtils.class);
	private static final String CONST_SERVICES = "Services";
	private static final int PHASE_LEVEL = 3;
	private static final int PRIORITY_LEVEL = 2;
	private static final int SERVICE_LEVEL = 1;

	/**
	 * Creates a set of {@link ServiceTag} objects from the taxonomy response. It
	 * works only on those records that are actually service tags, see:
	 * { @link isServiceTag(Tag) }.
	 * 
	 * @param taxonomyResponse {@link TaxonomyResponse} to build the service tags
	 *                         from. Assumed not <code>null</code>.
	 * @return {@link Set}<{@link ServiceTag}> with the results. Never
	 *         <code>null</code> but may be empty.
	 */
    public static Set<ServiceTag> buildServiceTaxonomy(TaxonomyResponse taxonomyResponse) {
    	try {
	    	List<Tag> phaseTags = taxonomyResponse
						    			.getTags()
						    			.stream()
						    			.filter(TaxonomyUtils::isPhaseTag)
						    			.collect(Collectors.toList());
	    	
	    	Map<String, ServiceTag> serviceTagMap = new HashMap<>();
	    	Set<String> priorityNames = new HashSet<>();
    	
    		phaseTags.stream().forEach(phaseTag -> {
	    		Tag priorityTag = phaseTag.getParent();
	    		priorityNames.add(priorityTag.getName());
	    		Tag serviceTag = priorityTag.getParent();
	
	    		if(serviceTagMap.containsKey(serviceTag.getName())) {
	    			
	    			ServiceTag service = serviceTagMap.get(serviceTag.getName());
	    			Optional<PriorityTag> existingPriority = service.getPriorities().stream()
							.filter(priorityIt -> priorityIt.getPriorityName().equals(priorityTag.getName()))
							.findFirst();
	 
	    			if(existingPriority.isPresent()) {
	    				existingPriority.get().addPhase(new PhaseTag(phaseTag.getId(), phaseTag.getName()));
	    			} else {
	    				service.addPriority(createPriority(priorityTag, phaseTag));
	    			}
	    		} else {
	        		serviceTagMap.put(serviceTag.getName(), createServiceTag(serviceTag, priorityTag, phaseTag));
	    		}
    		});
    		
            List<Tag> serviceTags = taxonomyResponse.getTags().stream().filter(TaxonomyUtils::isServiceTag)
                    .collect(Collectors.toList());
            Set<String> serviceNames = serviceTags.stream().map(Tag::getName).collect(Collectors.toSet());
            List<Tag> priorityTags = taxonomyResponse.getTags().stream().filter(TaxonomyUtils::isPriorityTag)
                    .collect(Collectors.toList());

            // For Adding priority, If service is also selected
            priorityTags.stream().forEach(tag -> {
                Tag serviceTag = tag.getParent();
                if (!priorityNames.contains(tag.getName())
                        && serviceNames.stream().anyMatch(serviceName -> serviceName.contains(serviceTag.getName()))) {
                    if (!serviceTagMap.containsKey(serviceTag.getName())) {
                        serviceTagMap.put(serviceTag.getName(), createServiceTag(serviceTag, tag));
                    } else {
                        serviceTagMap.get(serviceTag.getName()).addPriority(createPriority(tag));
                    }
                }
            });

            // Adding services, If only service is selected
            serviceTags.stream().forEach(tag -> {
                if (!serviceTagMap.containsKey(tag.getName())) {
                    serviceTagMap.put(tag.getName(), createServiceTag(tag));
                }
            });
            
    		return serviceTagMap.values().stream().collect(Collectors.toSet());
    	}
    	catch (Exception ex) {
    		LOGGER.warn("An error occurred while building taxonomy tags.", ex);
    		return Collections.EMPTY_SET;
    	}    	
    }
    
    private static PriorityTag createPriority(Tag priorityTag, Tag phaseTag) {
		PriorityTag priority = new PriorityTag(priorityTag.getId(), priorityTag.getName());
		priority.addPhase(new PhaseTag(phaseTag.getId(), phaseTag.getName()));
		return priority;
	}

	private static ServiceTag createServiceTag(Tag serviceTag, Tag priorityTag, Tag phaseTag) {
		ServiceTag service = new ServiceTag(serviceTag.getId(), serviceTag.getName());
		PriorityTag priority = new PriorityTag(priorityTag.getId(), priorityTag.getName());
		priority.addPhase(new PhaseTag(phaseTag.getId(), phaseTag.getName()));
		service.addPriority(priority);
		return service;
	}

	/**
	 * Checks if the given {@link Tag} represent a Phase tag. Phase tags have 4
	 * levels of deep and the root node is named "Services".
	 * 
	 * @param tag {@link Tag} to verify if it is a phase tag or not. Assumed not
	 *            <code>null</code>.
	 * @return <code>true</code> if the tag represents a phase tag.
	 *         <code>false</code> otherwise.
	 */
	public static boolean isPhaseTag(Tag tag) {
    	return isPhaseTag(tag, 0);
    }
    
    private static boolean isPhaseTag(Tag tag, int level) {
    	if(tag.getParent() == null) {
    		return (tag.getName().equals(CONST_SERVICES) 
    				&& level == PHASE_LEVEL); 
    	}else {
    		return isPhaseTag(tag.getParent(), ++level);
    	}
    }

	/**
	 * Returns the level of the Tag if the parameter tag has the same tagName and if it belongs to one of the required Levels
	 * @param tag: Tag that is going to be looped for matches.
	 * @param tagName: name of the Tag to search for
	 * @param requiredLevel HashSet: set of levels where the tagName should be found
	 * @return
	 */
    private static Integer isTag(Tag tag, String tagName, Set<Integer> requiredLevel){
		return isTag(tag, tagName, 0, requiredLevel);
	}

    private static Integer isTag(Tag tag, String tagName, Integer level, Set<Integer> requiredLevel) {
		if(tag.getParent() == null) {
			return (tag.getName().equals(tagName) && requiredLevel.contains(level)) ? level : null;
		} else {
			return isTag(tag.getParent(), tagName, ++level, requiredLevel);
		}
	}

	/**
	 * Returns a set of strings with the names of all Tags that belongs to the supported levels
	 * @param tags
	 * @param tagName
	 * @param supportedLevels
	 * @return
	 */
	public static Set<String> buildTaxonomyAgg(List<Tag> tags, String tagName, HashSet<Integer> supportedLevels){
		try {
			Set<String> result = new HashSet<>();

			for (Tag tag:
				 tags) {
				//Checks if the tag is part of the supported levels, and stores the deepest level to start looping
				Integer level = isTag(tag, tagName, supportedLevels);

				if(level != null) {
					// Based on the deepest level, it starts looping to store all tag names in "result"
					addTagNames(tag, level, supportedLevels, result);
				}
			}

			return result;

		} catch (Exception ex){
			LOGGER.warn("An error occurred while building taxonomy " + tagName + " tags.", ex);
			return Collections.EMPTY_SET;
		}
	}

	/**
	 * Provides support for multiple levels at the same time.
	 * Adds the tag name to the Set result, based on the supported levels
	 * @param tag
	 * @param level
	 * @param supportedLevels
	 * @param result
	 */
	private static void addTagNames(Tag tag, Integer level, HashSet<Integer> supportedLevels, Set<String> result) {
		if(supportedLevels.contains(level)) {
			result.add(tag.getName());
		}

		if(tag.getParent() != null){
			addTagNames(tag.getParent(), --level, supportedLevels, result);
		}
	}

	/**
	 * Builds topicsAgg with L3 taxonomies
	 * @param tags
	 * @return
	 */
	public static Set<String> buildTopicsAgg(List<Tag> tags){
		HashSet<Integer> supportedLevels = new HashSet<>();
		supportedLevels.add(TaxonomyLevelEnum.LEVEL_3.getLevel());

		return buildTaxonomyAgg(tags, TaxonomyDimensionEnum.BUSINESS_TOPICS.getName(), supportedLevels);
	}

	/**
	 * Builds industryAgg with L3 and L4 taxonomies
	 * @param tags
	 * @return
	 */
	public static Set<String> buildIndustryAgg(List<Tag> tags){
		HashSet<Integer> supportedLevels = new HashSet<>();
		supportedLevels.add(TaxonomyLevelEnum.LEVEL_2.getLevel());

		return buildTaxonomyAgg(tags, TaxonomyDimensionEnum.INDUSTRY.getName(), supportedLevels);
	}

	/**
	 * Builds industryAgg with L3 taxonomies
	 * @param tags
	 * @return
	 */
	public static Set<String> buildServiceAgg(List<Tag> tags){
		HashSet<Integer> supportedLevels = new HashSet<>();
		supportedLevels.add(TaxonomyLevelEnum.LEVEL_2.getLevel());

		return buildTaxonomyAgg(tags, TaxonomyDimensionEnum.SERVICES.getName(), supportedLevels);
	}

	/**
	 * Builds countryAgg with L3 and L4 taxonomies
	 * @param tags
	 * @return
	 */
	public static Set<String> buildCountryAgg(List<Tag> tags){
		HashSet<Integer> supportedLevels = new HashSet<>();
		supportedLevels.add(TaxonomyLevelEnum.LEVEL_3.getLevel());
		supportedLevels.add(TaxonomyLevelEnum.LEVEL_4.getLevel());

		return buildTaxonomyAgg(tags, TaxonomyDimensionEnum.COUNTRY.getName(), supportedLevels);
	}

    /**
     * Checks if the given {@link Tag} represent a Service tag. Service tags have 1
     * levels of deep and the root node is named "Services".
     * 
     * @param tag {@link Tag} to verify if it is a service tag or not. Assumed not
     *            <code>null</code>.
     * @return <code>true</code> if the tag represents a service tag.
     *         <code>false</code> otherwise.
     */
    public static boolean isServiceTag(Tag tag) {
        return isServiceTag(tag, 0);
    }

    private static boolean isServiceTag(Tag tag, int level) {
        if (tag.getParent() == null) {
            return (tag.getName().equals(CONST_SERVICES) && level == SERVICE_LEVEL);
        } else {
            return isServiceTag(tag.getParent(), ++level);
        }
    }

    /**
     * Checks if the given {@link Tag} represent a Priority tag. Priority tags have
     * 2 levels of deep and the root node is named "Services".
     * 
     * @param tag {@link Tag} to verify if it is a Priority tag or not. Assumed not
     *            <code>null</code>.
     * @return <code>true</code> if the tag represents a Priority tag.
     *         <code>false</code> otherwise.
     */
    public static boolean isPriorityTag(Tag tag) {
        return isPriorityTag(tag, 0);
    }

    private static boolean isPriorityTag(Tag tag, int level) {
        if (tag.getParent() == null) {
            return (tag.getName().equals(CONST_SERVICES) && level == PRIORITY_LEVEL);
        } else {
            return isPriorityTag(tag.getParent(), ++level);
        }
    }
    
    private static ServiceTag createServiceTag(Tag serviceTag) {
        return new ServiceTag(serviceTag.getId(), serviceTag.getName());
    }

    private static ServiceTag createServiceTag(Tag serviceTag, Tag priorityTag) {
        ServiceTag service = new ServiceTag(serviceTag.getId(), serviceTag.getName());
        PriorityTag priority = new PriorityTag(priorityTag.getId(), priorityTag.getName());
        service.addPriority(priority);
        return service;
    }
    
    private static PriorityTag createPriority(Tag priorityTag) {
        return new PriorityTag(priorityTag.getId(), priorityTag.getName());
    }

}
