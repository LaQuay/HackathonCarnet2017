'use strict';

const express = require('express');
const fs = require('fs');

const app = express();

/*---- Routes ----*/

app.get('/', function (req, res) {
    res.json({message: 'Hello world!'});
});

app.get('/microCities', function (req, res) {
    fs.readFile('./resources/micro-cities.json', 'utf8', function (err, data) {
        if (err) throw err;
        res.json(JSON.parse(data.toString()));
    });
});

/*---- Server ----*/

app.listen(process.env.PORT || 3000, function () {
    console.log('Server listening at port %s', process.env.PORT || 3000);
});