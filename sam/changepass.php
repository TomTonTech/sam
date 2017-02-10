<?php
$user=/*"cse01";//*/$_POST["username"];
$newP=/*"121212";//*/$_POST["newP"];
$curP=/*"123456";//*/$_POST["currentP"];
$user=strtolower($user);
$cpass=hash('sha512',$curP."tomton");
require "conc.php";
$sql="select * from login_credential where USERNAME='$user' and PASSWORD='$cpass'";
$res=$conn->query($sql);
if($row=$res->fetch_assoc())
{
	$pass=hash('sha512', $newP."tomton");
	$sql1="update login_credential set PASSWORD='$pass' where USERNAME='$user'";
	$res1=$conn->query($sql1);
	if($res1===TRUE)
	{
		echo 1;
	}
	else
	{
		echo 2;
	}
}
else
{
	echo 0;
}
?>