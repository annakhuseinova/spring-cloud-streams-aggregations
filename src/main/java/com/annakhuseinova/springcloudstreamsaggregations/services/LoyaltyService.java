package com.annakhuseinova.springcloudstreamsaggregations.services;

import com.annakhuseinova.springcloudstreamsaggregations.bindings.PosListenerBinding;
import com.annakhuseinova.springcloudstreamsaggregations.model.Notification;
import com.annakhuseinova.springcloudstreamsaggregations.model.PosInvoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableBinding(PosListenerBinding.class)
@RequiredArgsConstructor
public class LoyaltyService {

    private final RecordBuilder recordBuilder;

    @StreamListener("invoice-input-channel")
    @SendTo("notification-output-channel")
    public KStream<String, Notification> process(KStream<String, PosInvoice> input){
        KStream<String, Notification> notificationKStream = input.filter((key, value)-> value.getCustomerType()
                .equalsIgnoreCase("PRIME")).map((key, value)-> new KeyValue<>(value.getCustomerCardNo(),
                recordBuilder.getNotification(value)))
                .groupByKey()
                .reduce((aggValue, newValue)-> {
                    newValue.setTotalLoyaltyPoints(newValue.getEarnedLoyaltyPoints() + aggValue.getTotalLoyaltyPoints());
                    return newValue;
                }).toStream();
        notificationKStream.foreach((key, value)-> log.info(String.format("Notification: - Key: %s, Value: %s", key, value)));
        return notificationKStream;
    }
}
