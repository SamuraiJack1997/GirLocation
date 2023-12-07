<?php

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
    if (isset($_POST['id_zaposlenog'])) 
    {
    $id_zaposlenog = $_POST['id_zaposlenog'];
    $data=[];

    $sql=" SELECT l.naziv,lz.id_lokacije,l.geografska_sirina,l.geografska_duzina,l.dozvoljena_distanca_u_metrima FROM gir_lokacije l JOIN gir_lokacije_zaposleni lz ON l.id=lz.id_lokacije WHERE lz.id_zaposlenog=$id_zaposlenog "; 

    $results=$mysqli->query($sql);

        if($results->num_rows > 0){
            while($row=$results->fetch_assoc()){
                $data[]=$row;
            } 
            echo json_encode($data);       
        }else{
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