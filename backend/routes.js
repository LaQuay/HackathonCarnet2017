'use strict';

module.exports = function (app) {

    const foursquare = (require('foursquarevenues'))(process.env.FS_KEY, process.env.FS_SKEY);
    const fs = require('fs');

    /*---- Routes ----*/

    app.get('/bigiot/access/microcities', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            if (err) throw err;
            let microcities =JSON.parse(data.toString());
            function getVenues(callback) {
                let calls = microcities.length;
                microcities.forEach(function(microcity, index, microcities) {
                    let positionQuerry = {ll : microcity.coordinates.lat + ',' + microcity.coordinates.lng};
                    foursquare.getVenues(positionQuerry, function (err2, results) {
                        treatError(res, err2);

                        microcities[index].venues=buildFilteredVenues(results.response.venues);
                        let carServices = JSON.parse(fs.readFileSync('./resources/car-services.json', 'utf8'));
                        carServices.forEach(function(carService){
                            /*add location to car services*/
                            carService.location.lat=microcity.coordinates.lat;
                            carService.location.lng=microcity.coordinates.lng;
                            microcities[index].venues.push(carService);
                        });
                        --calls;
                        if (calls==0) callback();
                    });
                });
            }
            getVenues(function(){
                //this will be run after getVenues is finished.
                res.json(microcities);
                // Rest of your code here.
            });
        });
    });

    app.get('/bigiot/access/services/microcitieslist', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            treatError(res, err);
            res.json(JSON.parse(data.toString()));
        });
    });

    app.get('/bigiot/access/services', function (req, res) {
        foursquare.getVenues(req.query, function (err, results) {
            treatError(res, err);
            res.json(buildFilteredVenues(results.response.venues));
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

    function treatError(res, err) {
        if (err) {
            if (err.errorType === 'param_error') res.status(400);
            else {
                console.log(err);
                res.status(500);
            }
        }
    }

};