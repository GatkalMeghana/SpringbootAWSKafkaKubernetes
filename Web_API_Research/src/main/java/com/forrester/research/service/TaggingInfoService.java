package com.forrester.research.service;

import com.forrester.research.entity.AccessInfo;
import com.forrester.research.entity.TaggingInfo;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;

public interface TaggingInfoService {

    TaggingInfo getTaggingInfo(String contentId);

    AccessInfo getClientAccess(String contentIds,  String userEmail) throws DataNotFoundException, AuthorizationException;
}
