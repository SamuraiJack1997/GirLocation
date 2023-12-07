<?php
// $update = json_decode($_POST["update"]);

$username = "gir_test";
$password = "ihcc5yh4n6ep";
$hostname = "localhost";
$db = "gir_test";
$connected=false;
$sql="";

$mysqli = new mysqli($hostname,$username,$password,$db);
if ($mysqli -> connect_errno) {
    echo "Failed to connect to MySQL: " . $mysqli -> connect_error;
    exit();
}
else{
    // array for JSON response
    $response = array();
 
    // check for required fields
    if (isset($_POST['mac_adresa'])) 
    {
    $mac_adresa = $_POST['mac_adresa'];

    $sql="INSERT INTO gir_prijavljeni_uredjaji (id_uredjaja) VALUES ('$mac_adresa')"; 
    
    if ($mysqli->query($sql) === TRUE) {
        echo 'true';
        } else {
        echo 'false';
        }

    } else {
        // required field is missing
        $response["success"] = 0;
        $response["message"] = "Required field(s) is missing";
 
        // echoing JSON response
        echo json_encode($response);
    }
    
    $mysqli -> close();
    }
?>