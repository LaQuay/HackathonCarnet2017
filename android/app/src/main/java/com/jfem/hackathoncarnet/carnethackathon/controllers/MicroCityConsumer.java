package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
/*
public class MicroCityConsumer {
    private final String TAG = MicroCityConsumer.class.getSimpleName();

    private static String MARKETPLACE_URI = "http://gibo.fib.upc.edu:50002";
    private static String CONSUMER_ID = "Barcelona_City-Example_Service";

    private ArrayList<MicroCity> microCities;

    public void getMicrocities() throws InterruptedException, ExecutionException, IOException, IncompleteOfferingQueryException, AccessToNonSubscribedOfferingException {
        Consumer consumer = new Consumer(CONSUMER_ID, MARKETPLACE_URI);
        consumer.authenticate("12345678");

        OfferingQuery query = OfferingQuery.create("BikesQuery")
                .withInformation(new Information("Bikes Offering", "bigiot:Bike")) // Query type matches bike sharing offering description
                .inRegion(Region.city("Barcelona")) // In Barcelona region
                .withAccountingType(BigIotTypes.AccountingType.PER_ACCESS)
                .withMaxPrice(Price.Euros.amount(0.005))
                .withLicenseType(BigIotTypes.LicenseType.OPEN_DATA_LICENSE);

        // Discover offering
        CompletableFuture<List<SubscribableOfferingDescription>> listFuture = consumer.discover(query);
        List<SubscribableOfferingDescription> list = listFuture.get();

        listFuture.thenApply(SubscribableOfferingDescription::showOfferingDescriptions);

        consumer.discover(query,(q,l)-> {
            log("Discovery with callback");
            SubscribableOfferingDescription.showOfferingDescriptions(list);
        });

        // Just select the first Offering in the list
        SubscribableOfferingDescription selectedOfferingDescription = list.get(0);

        // Instantiation of Offering Access objects via subscribe
        CompletableFuture<Offering> offeringFuture = selectedOfferingDescription.subscribe();
        Offering offering = offeringFuture.get();

        // Prepare access parameters
        AccessParameters accessParameters = AccessParameters.create()
                .addRdfTypeValue(new RDFType("schema:city"), "bcn");

        CompletableFuture<AccessResponse> response = offering.accessOneTime(accessParameters);

        AccessResponse result = response.get();
        Log.e("MCC","One-time access result = " + result.toString());

        //Thread.sleep(60000);

        //offering.unsubscribe();
    }
}
*/