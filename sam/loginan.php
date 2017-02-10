<?php
require_once "conc.php";
// Check whether username or password is set from android	
/*$_POST["username"]="cse01";
$_POST["password"]="121212";*/

if(isset($_POST['username']) && isset($_POST['password']) && $_POST["username"]!=""&&$_POST["password"]!="")
{
// Innitialize Variable
  $info = array();
  $result='';
  $username = $_POST['username'];
  $password = $_POST['password'];
  $hashpass=hash('sha512',$password."tomton");
  // Query database for row exist or not
  $sql = "SELECT * FROM login_credential WHERE  USERNAME = '$username' AND PASSWORD = '$hashpass'";
  $res = $conn->query($sql);
  if($row=$res->fetch_assoc())
  {
    $name=$row["NAME"];
    $prio=$row["PRIORITY"];
    $result="true";
    $info=array(
    'name'=>$name,
    'username'=>$username,
    'priority'=>$prio,
    'authentication'=>'success');
    echo json_encode($info);
  }  
  else
  {
    echo "unsuccess";
  }

// send result back to android
}
else
{
  echo "unsuccess";
}
?>