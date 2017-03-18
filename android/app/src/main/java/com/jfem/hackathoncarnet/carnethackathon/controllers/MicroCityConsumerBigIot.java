package com.jfem.hackathoncarnet.carnethackathon.controllers;

//Unused because of high CPU usage, in some PCs it takes more than 20min to compile
//If you want to try it, please use Java 8 in gradle file
public class MicroCityConsumerBigIot {
    private static final String TAG = MicroCityConsumerBigIot.class.getSimpleName();
    private static String MARKETPLACE_URI = "http://gibo.fib.upc.edu:50002";
    private static String CONSUMER_ID = "Barcelona_City-Example_Service";

    /*public static void getMicrocities() throws InterruptedException, ExecutionException, IOException, IncompleteOfferingQueryException, AccessToNonSubscribedOfferingException {
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
    }*/
}
