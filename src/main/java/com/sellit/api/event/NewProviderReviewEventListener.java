package com.sellit.api.event;

import com.sellit.api.provider.entity.ProviderEntity;
import com.sellit.api.provider.entity.ProviderRatingEntity;
import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.provider.repository.ProviderRatingRepository;
import com.sellit.api.provider.repository.ProviderReviewLogRepository;
import com.sellit.api.utils.UuidGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Slf4j
public class NewProviderReviewEventListener implements ApplicationListener<NewProviderReviewEvent> {
    final private ProviderReviewLogRepository providerReviewLogRepository;
    final private ProviderRatingRepository providerRatingRepository;

    public NewProviderReviewEventListener(ProviderReviewLogRepository providerReviewLogRepository, ProviderRatingRepository providerRatingRepository) {
        this.providerReviewLogRepository = providerReviewLogRepository;
        this.providerRatingRepository = providerRatingRepository;
    }

    @SneakyThrows
    @Override
    @Transactional
    public synchronized void onApplicationEvent(NewProviderReviewEvent newProviderReviewEvent) {
        ProviderReviewLogEntity providerReviewLogEntity = providerReviewLogRepository.findByUuid(newProviderReviewEvent.getProverReviewLogUuid()).orElseThrow(
                ()->new EntityNotFoundException("No review log found with the provided identifier")
        );

        ProviderEntity providerEntity = providerReviewLogEntity.getServiceAppointment().getServiceDeliveryOffer().getServiceProvider().getProvider();

        log.info("Updating The overall rating for providerEntity : {}", providerEntity.getUuid());

        ProviderRatingEntity providerRatingEntity = providerEntity.getProviderRating();

        ProviderRatingEntity overallProviderRatingEntity = new ProviderRatingEntity();

        if(providerRatingEntity != null){
            overallProviderRatingEntity.setAvgPriceRating((providerReviewLogEntity.getAvgPriceRating()+ providerRatingEntity.getAvgPriceRating())/2.0);
            overallProviderRatingEntity.setAvgCommunicationRating((providerReviewLogEntity.getAvgCommunicationRating()+ providerRatingEntity.getAvgCommunicationRating())/2.0);
            overallProviderRatingEntity.setAvgProfessionalismRating((providerReviewLogEntity.getAvgProfessionalismRating()+ providerRatingEntity.getAvgProficiencyRating())/2.0);
            overallProviderRatingEntity.setAvgPunctualityRating((providerReviewLogEntity.getAvgPunctualityRating()+ providerRatingEntity.getAvgPunctualityRating())/2.0);
            overallProviderRatingEntity.setAvgProficiencyRating((providerReviewLogEntity.getAvgProficiencyRating()+ providerRatingEntity.getAvgProficiencyRating())/2.0);

            // Get the average rating
            double overallScore = ((providerReviewLogEntity.getAvgPriceRating()+ providerRatingEntity.getAvgPriceRating())+(providerReviewLogEntity.getAvgCommunicationRating()+ providerRatingEntity.getAvgCommunicationRating())
                    +(providerReviewLogEntity.getAvgProfessionalismRating()+ providerRatingEntity.getAvgProficiencyRating())
                    +(providerReviewLogEntity.getAvgPunctualityRating()+ providerRatingEntity.getAvgPunctualityRating())
                    +(providerReviewLogEntity.getAvgProficiencyRating()+ providerRatingEntity.getAvgProficiencyRating()))/5.0;
            overallProviderRatingEntity.setOverallRating(overallScore);
        }else {
            overallProviderRatingEntity.setAvgPriceRating(providerReviewLogEntity.getAvgPriceRating());
            overallProviderRatingEntity.setAvgCommunicationRating(providerReviewLogEntity.getAvgCommunicationRating());
            overallProviderRatingEntity.setAvgProfessionalismRating(providerReviewLogEntity.getAvgProfessionalismRating());
            overallProviderRatingEntity.setAvgPunctualityRating(providerReviewLogEntity.getAvgPunctualityRating());
            overallProviderRatingEntity.setAvgProficiencyRating(providerReviewLogEntity.getAvgProficiencyRating());

            // Get the average rating
            double overallScore = (providerReviewLogEntity.getAvgPriceRating()+ providerReviewLogEntity.getAvgCommunicationRating()
                    + providerReviewLogEntity.getAvgProfessionalismRating()
                    + providerReviewLogEntity.getAvgPunctualityRating()
                    + providerReviewLogEntity.getAvgProficiencyRating())/5.0;
            overallProviderRatingEntity.setOverallRating(overallScore);
        }
        overallProviderRatingEntity.setUuid(UuidGenerator.generateRandomString(12));
        overallProviderRatingEntity.setUpdatedOn(new Date());
        overallProviderRatingEntity.setProvider(providerEntity);
        providerEntity.setProviderRating(overallProviderRatingEntity);
        providerRatingRepository.save(overallProviderRatingEntity);
    }
}
