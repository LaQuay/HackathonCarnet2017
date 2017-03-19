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

    app.get('/bigiot/access/microcities/:id/services', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            const status = treatError(err);
            if (status === 200) {
                const microCityID = req.params.id - 1;
                const microCities = JSON.parse(data.toString());

                if (microCityID < 0 || microCityID >= microCities.length) return res.sendStatus(400);

                const params = {
                    ll: microCities[microCityID].coordinates.lat + ',' + microCities[microCityID].coordinates.lng,
                };

                foursquare.getVenues(params, function (err, results) {
                    if (treatError(res, err)) {
                        res.json(buildFilteredVenues(results.response.venues));
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
                res.json(buildFilteredVenues(results.response.venues));
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
                    lat: venue.location.lat,
                    lng: venue.location.lng,
                    distance: venue.location.distance
                },
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