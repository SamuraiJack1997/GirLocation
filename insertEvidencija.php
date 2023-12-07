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
    if (isset($_POST['id_zaposlenog']) && isset($_POST['id_lokacije']) && isset($_POST['IN_OUT']) && isset($_POST['in_out_geografska_sirina']) && isset($_POST['in_out_geografska_duzina'])) 
    {
    $id_zaposlenog = $_POST['id_zaposlenog'];
    $id_lokacije = $_POST['id_lokacije'];
    $IN_OUT = $_POST['IN_OUT'];
    $in_out_geografska_sirina = $_POST['in_out_geografska_sirina'];
    $in_out_geografska_duzina = $_POST['in_out_geografska_duzina'];
    

    $sql="INSERT INTO gir_evidencija (id_zaposlenog, id_lokacije, IN_OUT, in_out_geografska_sirina, in_out_geografska_duzina) VALUES ($id_zaposlenog,$id_lokacije,$IN_OUT,$in_out_geografska_sirina,$in_out_geografska_duzina)"; 
    
    //echo $sql;

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