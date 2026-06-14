package com.forrester.research.utils;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.clients.permissions.PermissionsClient;
import com.forrester.research.clients.permissions.response.ResearchPermission;
import com.forrester.research.clients.user.UserClient;
import com.forrester.research.clients.user.response.UserInfo;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserPermissionUtil {

    @Autowired
    private UserClient userClient;

    @Autowired
    private PermissionsClient permissionsClient;

    @Value("${internal.host}")
    private List<String> internalHosts;

    /**
     * Get the permissions for the entryId
     *
     * @param headers MultiValueMap from String to String
     * @param entryId String
     * @return ResearchPermission
     * @throws AuthorizationException Exception
     * @throws DataNotFoundException Exception
     */
    @LogThis
    public ResearchPermission validateTokenAndReturnPermission(MultiValueMap<String, String> headers, String entryId) throws AuthorizationException, DataNotFoundException {
        ResearchPermission permission = new ResearchPermission(entryId, false);
        permission.setHasAccess(internalHosts.contains(headers.getFirst(HttpHeaders.HOST.toLowerCase())));
        //check id token and check user access.
        if (headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()) != null) {
            UserInfo userInfo = userClient.getUserDetails(headers.getFirst(HttpHeaders.AUTHORIZATION.toLowerCase()), null);
            if (userInfo != null && userInfo.getUserId() > 0) {
                List<ResearchPermission> researchPermission = permissionsClient.getResearchPermission(
                        headers.getFirst(HttpHeaders.AUTHORIZATION.toLowerCase()), Collections.singletonList(entryId), null);
                if (!researchPermission.isEmpty() && researchPermission.get(0) != null) {
                    researchPermission.get(0).setUserInfo(userInfo);
                    return researchPermission.get(0);
                }
            }
        }
        return permission;
    }

    /**
     * Get the permissions for the entryIds
     *
     * @param headers MultiValueMap from String to String
     * @param entryIds String[]
     * @param metaDataOnly boolean
     * @return List of ResearchPermission
     * @throws AuthorizationException Exception
     * @throws DataNotFoundException Exception
     */
    @LogThis
    public List<ResearchPermission> validateTokenAndReturnPermissions(MultiValueMap<String, String> headers,
                                                                      String[] entryIds, boolean metaDataOnly) throws AuthorizationException, DataNotFoundException {
        boolean defaultAccess = internalHosts.contains(headers.getFirst(HttpHeaders.HOST.toLowerCase()));
        if (defaultAccess && metaDataOnly) {
            defaultAccess = false;
        }
        boolean defaultAccessForAll = defaultAccess;
        List<ResearchPermission> defaultPermissions = Arrays.stream(entryIds)
                .map(entryId -> new ResearchPermission(entryId, defaultAccessForAll)).collect(Collectors.toList());

        //check id token and check user access.
        //NOTE - Currently will not be used. Revisit when making use of authorization token.
        if (headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()) != null) {
            UserInfo userInfo = userClient.getUserDetails(headers.getFirst(HttpHeaders.AUTHORIZATION.toLowerCase()), null);
            if (userInfo != null && userInfo.getUserId() > 0) {
                List<ResearchPermission> permissions = permissionsClient.getResearchPermission(
                        headers.getFirst(HttpHeaders.AUTHORIZATION.toLowerCase()), Arrays.asList(entryIds), null);
                if (!CollectionUtils.isEmpty(permissions)) {
                    return permissions;
                }
            }
        }
        return defaultPermissions;
    }

    /**
     * Get the permissions for the entryIds
     *
     * @param email String
     * @param entryIds String
     * @return List of ResearchPermission
     * @throws AuthorizationException Exception
     * @throws DataNotFoundException Exception
     */
    @LogThis
    public List<ResearchPermission> getPermissionsByContentIdAndUserEmail(String email,
                                                                          String entryIds) throws AuthorizationException, DataNotFoundException {

        UserInfo userInfo = userClient.getUserDetails(null, email);
        if (userInfo != null && userInfo.getUserId() > 0) {
            List<ResearchPermission> permissions = permissionsClient.getResearchPermission(
                    null, Arrays.asList(entryIds.split(",")), userInfo.getUserId());
            if (!CollectionUtils.isEmpty(permissions)) {
                return permissions;
            } else {
                throw new DataNotFoundException(Constants.PERMISSION_INFORMATION_NOT_FOUND_FOR_EMAIL_CONTENT_ID);
            }
        } else {
            throw new DataNotFoundException(Constants.USER_NOT_FOUND_FOR_EMAIL);
        }

    }

    /**
     * Get a map with the entryId/contentId as key and its corresponding hasAccess flag as the value
     *
     * @param permissions List of ResearchPermission
     * @return Map from String to Boolean
     */
    public Map<String, Boolean> getAccessFromPermissions(List<ResearchPermission> permissions) {
        return permissions.stream().collect(Collectors.toMap(ResearchPermission::getId, ResearchPermission::isHasAccess, (hasAccess1, hasAccess2) -> hasAccess1));
    }
}