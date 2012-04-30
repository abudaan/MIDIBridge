<?php


$tmpFile = "p:/tmp.mid";

file_put_contents(
    $tmpFile, file_get_contents("php://input",r)
);

//file_put_contents("p:/base64.mid", base64_encode(file_get_contents($tmpFile)));


echo base64_encode(file_get_contents($tmpFile));

?>
