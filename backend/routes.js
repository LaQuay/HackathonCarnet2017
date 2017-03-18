module.exports = function (app) {

    const foursquare = (require('foursquarevenues'))(process.env.FS_KEY, process.env.FS_SKEY);
    const fs = require('fs');

    app.get('/bigiot/access/microcities', function (req, res) {
        fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
            if (err) throw err;
            res.json(JSON.parse(data.toString()));
        });
    });

    app.get('/services', function (req, res) {
        const params = {
            ll: req.query.lat + ',' + req.query.lng
        };
        foursquare.getVenues(params, function (err, venues) {
            if (err) {
                if (err.errorType === 'param_error') res.status(400);
                else {
                    console.log(err);
                    res.status(500);
                }
            }
            res.json(venues);
        });
    });

};