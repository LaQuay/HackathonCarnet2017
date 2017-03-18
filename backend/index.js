'use strict';

require('dotenv').config();
const express = require('express');

const app = express();
require('./routes')(app);

app.listen(process.env.PORT || 3000, function () {
    console.log('Server listening at port %s', process.env.PORT || 3000);
});