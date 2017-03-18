const express = require('express');
const app = express();

const PORT = 3000;

/*---- Routes ----*/

app.get('/', function (req, res) {
    res.json({message: 'Hello world!'});
});

/*---- Server ----*/

app.listen(PORT, function () {
    console.log("Server listening at port %s", PORT);
});