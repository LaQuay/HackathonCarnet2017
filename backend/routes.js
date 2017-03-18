'use strict';

module.exports = function (app) {

    const foursquare = (require('foursquarevenues'))(process.env.FS_KEY, process.env.FS_SKEY);
    const fs = require('fs');

    /*---- Routes ----*/

    app.get('/bigiot/access/microcities', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            if (err) throw err;
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