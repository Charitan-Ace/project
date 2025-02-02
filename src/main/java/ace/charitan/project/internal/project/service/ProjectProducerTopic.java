package ace.charitan.project.internal.project.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum ProjectProducerTopic {
    PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE("project-geography-get-country-by-iso-code"),
    PROJECT_MEDIA_GET_MEDIA_BY_PROJECT_ID("project-media-get-media-by-project-id"),
    PROJECT_DONATION_GET_DONATION_BY_PROJECT_ID("donation.get.projectId"),
    // PROJECT_SUBSCRIPTION_GET_MEDIA_BY_PROJECT_ID("project-media-get-media-by-project-id");
    PROJECT_SUBSCRIPTION_NEW_PROJECT("project-subscription-new-project"),
    PROJECT_DONATION_GET_PROJECTS_BY_DONOR_ID("donation.project-ids-by-donor-id");

    private String topic;

}
