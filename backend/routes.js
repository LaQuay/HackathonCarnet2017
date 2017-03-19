'use strict';

module.exports = function (app) {

    const foursquare = (require('foursquarevenues'))(process.env.FS_KEY, process.env.FS_SKEY);
    const fs = require('fs');

    /*---- Routes ----*/

    app.get('/bigiot/access/microcities', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            const status = treatError(err);
            if (status === 200) {
                res.json(JSON.parse(data.toString()));
            } else {
                res.sendStatus(status);
            }
        });
    });

    app.get('/bigiot/access/promotions', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, datacities) {
            const status = treatError(err);
            if (status === 200) {
                let query = null;
                if (req.query.query != null) query = req.query.query;
                //console.log(query);
                const microCities = JSON.parse(datacities.toString());
                fs.readFile('./resources/promotions.json', 'utf8', function (err, dataPromotions) {
                    const status = treatError(err);
                    if (status === 200) {
                        const promotions = JSON.parse(dataPromotions.toString());
                        let returnedPromotions = [];
                        promotions.forEach(function (promotion) {
                            if (query === promotion.category || query == null) {
                                const microcityID = promotion.microcity - 1;
                                returnedPromotions.push({
                                    "microcity": microCities[microcityID],
                                    "discount": promotion.discount,
                                    "service": promotion.service
                                });
                            }
                        });
                        res.json(returnedPromotions.sort(function (a, b) {
                            return parseFloat(a.service.location.distance) - parseFloat(b.service.location.distance);
                        }));
                    } else {
                        res.sendStatus(status);
                    }
                });
            } else {
                res.sendStatus(status);
            }
        });
    });

    app.get('/bigiot/access/microcities/:id/promotions', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, datacities) {
            const status = treatError(err);
            if (status === 200) {
                let query = null;
                if (req.query.query != null) query = req.query.query;
                const microCityID = req.params.id - 1;
                const microCities = JSON.parse(datacities.toString());
                fs.readFile('./resources/promotions.json', 'utf8', function (err, dataPromotions) {
                    const status = treatError(err);
                    if (status === 200) {
                        const promotions = JSON.parse(dataPromotions.toString());
                        let returnedPromotions = [];
                        promotions.forEach(function (promotion) {
                            if (promotion.microcity - 1 === microCityID) {
                                if (query === promotion.category || query == null) {
                                    returnedPromotions.push({
                                        "microcity": microCities[microCityID],
                                        "discount": promotion.discount,
                                        "service": promotion.service
                                    });
                                }
                            }
                        });
                        res.json(returnedPromotions.sort(function (a, b) {
                            return parseFloat(a.service.location.distance) - parseFloat(b.service.location.distance);
                        }));
                    } else {
                        res.sendStatus(status);
                    }
                });
            } else {
                res.sendStatus(status);
            }
        });
    });

    app.get('/bigiot/access/microcities/:id/services', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            const status = treatError(err);
            if (status === 200) {
                const microCityID = req.params.id - 1;
                const microCities = JSON.parse(data.toString());

                if (microCityID < 0 || microCityID >= microCities.length || microCities[microCityID] === undefined) {
                    return res.sendStatus(400);
                }

                const params = {
                    ll: microCities[microCityID].coordinates.lat + ',' + microCities[microCityID].coordinates.lng,
                };

                foursquare.getVenues(params, function (err, results) {
                    if (treatError(res, err)) {
                        let carServices = JSON.parse(fs.readFileSync('./resources/car-services.json', 'utf8'));
                        carServices.forEach(function(carService){
                            /*add location to car services*/
                            carService.location.lat=microCities[microCityID].coordinates.lat;
                            carService.location.lng=microCities[microCityID].coordinates.lng;
                            results.response.venues.push(carService);
                        });

                        res.json(buildFilteredVenues(results.response.venues).sort(function(a, b) {
                            return parseFloat(a.location.distance) - parseFloat(b.location.distance);
                        }));
                    }
                });
            } else {
                res.sendStatus(status);
            }
        });
    });

    app.get('/bigiot/access/services', function (req, res) {
        foursquare.getVenues(req.query, function (err, results) {
            const status = treatError(err);
            if (status === 200) {
                res.json(buildFilteredVenues(results.response.venues).sort(function(a, b) {
                    return parseFloat(a.location.distance) - parseFloat(b.location.distance);
                }));
            } else {
                res.sendStatus(status);
            }
        });
    });

    app.get('/bigiot/access/services/:id/photos', function (req, res) {
        const params = {
            venue_id: req.params.id + '/photos'
        };
        foursquare.getVenue(params, function (err, results) {
            const status = treatError(err);
            if (status === 200) {
                res.json(results.response.photos.items[0]);
            } else {
                res.sendStatus(status);
            }
        });
    });

    /*---- Auxiliary functions ----*/

    function buildFilteredVenues(venues) {
        let filteredVenues = [];
        venues.forEach(function (venue) {
            filteredVenues.push({
                id: venue.id,
                name: venue.name,
                location: {
                    address: venue.location.address,
                    formattedAddress: venue.location.formattedAddress,
                    lat: venue.location.lat,
                    lng: venue.location.lng,
                    distance: venue.location.distance
                },
                stats: venue.stats,
                url: venue.url,
                contact: venue.contact,
                categories: buildFilteredCategories(venue.categories)
            });
        });
        return filteredVenues;
    }

    function buildFilteredCategories(categories) {
        let filteredCategories = [];
        categories.forEach(function (category) {
            filteredCategories.push({
                id: category.id,
                name: category.name
            });
        });
        return filteredCategories;
    }

    function treatError(err) {
        if (err) {
            console.log(err.errorType);
            if (err.errorType === 'param_error') return 400;
            else if (err.errorType === 'invalid_auth') return 403;
            else return 500;
        }
        return 200;
    }

};