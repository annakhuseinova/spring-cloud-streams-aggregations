package com.annakhuseinova.springcloudstreamsaggregations.bindings;

import com.annakhuseinova.springcloudstreamsaggregations.model.PosInvoice;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

import javax.management.Notification;

public interface PosListenerBinding {

    @Input("invoice-input-channel")
    KStream<String, PosInvoice> invoiceInputStream();

    @Output("notification-output-channel")
    KStream<String, Notification> notificationOutputStream();
}
