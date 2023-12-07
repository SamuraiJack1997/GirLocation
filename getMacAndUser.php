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
    if (isset($_POST['mac_adresa'])) 
    {
    $mac_adresa = $_POST['mac_adresa'];
    $data=[];

    $sql=" SELECT z.ime,z.prezime,u.id_uredjaja,u.id_zaposlenog,u.aktivan FROM gir_prijavljeni_uredjaji u JOIN gir_zaposleni z ON u.id_zaposlenog=z.id WHERE u.id_uredjaja like '$mac_adresa'; "; 

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