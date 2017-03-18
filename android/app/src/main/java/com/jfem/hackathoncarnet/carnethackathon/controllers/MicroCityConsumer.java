package com.jfem.hackathoncarnet.carnethackathon.controllers;

import android.util.Log;

import org.bigiot.lib.ConsumerCore;
import org.bigiot.lib.exceptions.AccessToNonActivatedOfferingException;
import org.bigiot.lib.exceptions.AccessToNonSubscribedOfferingException;
import org.bigiot.lib.exceptions.IllegalAccessParameterException;
import org.bigiot.lib.exceptions.IncompleteOfferingQueryException;
import org.bigiot.lib.handlers.AccessResponseFailureHandler;
import org.bigiot.lib.handlers.AccessResponseSuccessHandler;
import org.bigiot.lib.handlers.DiscoverFailureException;
import org.bigiot.lib.handlers.DiscoverResponseErrorHandler;
import org.bigiot.lib.handlers.DiscoverResponseHandler;
import org.bigiot.lib.model.BigIotTypes;
import org.bigiot.lib.model.Information;
import org.bigiot.lib.model.Price;
import org.bigiot.lib.model.RDFType;
import org.bigiot.lib.offering.AccessParameters;
import org.bigiot.lib.offering.AccessResponse;
import org.bigiot.lib.offering.IOfferingCore;
import org.bigiot.lib.offering.OfferingCore;
import org.bigiot.lib.offering.SubscribableOfferingDescriptionCore;
import org.bigiot.lib.query.IOfferingQuery;
import org.bigiot.lib.query.OfferingQuery;
import org.bigiot.lib.query.elements.Region;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MicroCityConsumer {
    private static final String TAG = MicroCityConsumer.class.getSimpleName();
    private static String MARKETPLACE_URI = "http://gibo.fib.upc.edu:50002";
    private static String CONSUMER_ID = "Barcelona_City-Example_Service";

    public static void getMicrocities() throws InterruptedException, ExecutionException, IOException, IncompleteOfferingQueryException, AccessToNonSubscribedOfferingException {
        ConsumerCore consumerCore = new ConsumerCore(CONSUMER_ID, MARKETPLACE_URI);
        consumerCore.authenticate("12345678");

        OfferingQuery offeringQuery = OfferingQuery.create("BikesQuery")
                .withInformation(new Information("Bikes Offering", "bigiot:Bike")) // Query type matches bike sharing offering description
                .inRegion(Region.city("Barcelona")) // In Barcelona region
                .withAccountingType(BigIotTypes.AccountingType.PER_ACCESS)
                .withMaxPrice(Price.Euros.amount(0.005))
                .withLicenseType(BigIotTypes.LicenseType.OPEN_DATA_LICENSE);

        if (!offeringQuery.isValid()) {
            Log.e(TAG, "QUERY NOT VALID");
        }

        consumerCore.discover(offeringQuery,
                new DiscoverResponseHandler() {
                    @Override
                    public void processResponse(IOfferingQuery reference, List offeringDescriptions) {
                        Log.e(TAG, "Discovered: " + offeringDescriptions.size() + " offerings");
                        for (int i = 0; i < offeringDescriptions.size(); ++i) {
                            SubscribableOfferingDescriptionCore subscribableOfferingDescriptionCore = (SubscribableOfferingDescriptionCore) offeringDescriptions.get(i);
                            Log.e(TAG, "Offering " + subscribableOfferingDescriptionCore.getName());

                            OfferingCore concreteOffering = subscribableOfferingDescriptionCore.subscribeBlocking();

                            AccessParameters accessParameters = AccessParameters.create()
                                    .addRdfTypeValue(new RDFType("schema:city"), "bcn");

                            try {
                                concreteOffering.accessOneTime(accessParameters,
                                        new AccessResponseSuccessHandler() {
                                            @Override
                                            public void processResponseOnSuccess(IOfferingCore iOfferingCore, AccessResponse accessResponse) {
                                                Log.e(TAG, "One time Offering access: " + accessResponse.asJsonNode().get("stations").size() + " elements received. ");
                                            }
                                        }, new AccessResponseFailureHandler() {
                                            @Override
                                            public void processResponseOnFailure(IOfferingCore iOfferingCore, AccessResponse accessResponse) {
                                                Log.e(TAG, "processResponseOnFailure - failure");
                                            }
                                        });
                            } catch (IllegalAccessParameterException | AccessToNonActivatedOfferingException | AccessToNonSubscribedOfferingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new DiscoverResponseErrorHandler() {
                    @Override
                    public void processResponse(IOfferingQuery iOfferingQuery, DiscoverFailureException e) {
                        Log.e(TAG, "processResponse - failure");
                    }
                }
        );
    }
}
