package com.forrester.research.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.ContentfulClient;
import com.forrester.research.clients.contentful.response.ContentfulModelStore;
import com.forrester.research.clients.contentful.response.models.PriceConfig;
import com.forrester.research.clients.contentful.response.models.Research;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.ServiceException;

@Component
public class ResearchPriceUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchPriceUtil.class);
	@Autowired
	private ContentfulModelStore contentfulModelStore;
	@Autowired
	private ContentfulClient contentfulClient;

	/**
	 * This method update the price based on Price type for a document
	 * @param research Research
	 */
	public void getPriceByType(Research research){
		String priceType;
		if (research.getPriceType() != null) {
			priceType = research.getPriceType();
		} else {
			priceType = Constants.PRICE_STANDARD;
			research.setPriceType(priceType);
		}
		switch (priceType) {
		case Constants.PRICE_STANDARD:
			String price = calculatePrice (research);
			if(price.equalsIgnoreCase(Constants.PRICE_NOT_FOR_SALE)) {
				research.setPriceType(Constants.PRICE_NOT_FOR_SALE);
				research.setPrice(null);
			}else {
				research.setPrice(Double.valueOf (price.replace(",", "")));
			}
			break;
		case Constants.PRICE_FREE:
			research.setPrice(0.0);
			break;
		case Constants.PRICE_NOT_FOR_SALE:
			research.setPriceType(Constants.PRICE_NOT_FOR_SALE);
			research.setPrice(null);
			break;
		default:
			break;
		}
	}

	/**
	 * This method calculate the price for standard price type based on ipType
	 * @param research Research
	 * @return String
	 */
	public String calculatePrice(Research research) {
		Class<?> contentModel = contentfulModelStore.getModel(Constants.PRICE_CONFIG);
		String ipType = research.getIpType();
		try {
			if(StringUtils.isNotBlank(ipType)) {
			LOGGER.info("Quering contentful for price of {} ", research.getContentId());
			PriceConfig priceConfig = (PriceConfig) contentfulClient.getContentDetailsByField(contentModel, "title", ipType);
			return priceConfig.getPrice();
			}
			else {
				return Constants.PRICE_NOT_FOR_SALE;
			}
		} catch (DataNotFoundException dnfe) {
			LOGGER.error("Error calculating price : Unable to find price value for given ipType : {}. Exception: ", ipType, dnfe);
		} catch (ServiceException e) {
			LOGGER.error("Error calculating price .Exception: ",e);
		}
		return Constants.PRICE_NOT_FOR_SALE;
	}
}