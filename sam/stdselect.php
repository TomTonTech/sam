<?php
require_once "conc.php";
$sub=/*"cs2k602";//*/$_POST["subject"];
require_once "yearsearch.php";
$yr=yearsearch($sub);
$branch=branchsearch($sub);
$div=/*"A";//*/$_POST["division"];
$tablename=$branch."_student";
$ar=array();
$sql="select NAME,ROLLNO from ".$tablename." where DIVISION='$div' and YEAR='$yr' order by ROLLNO";
$res=$conn->query($sql);
$i=0;
while($re=$res->fetch_assoc())
{
	$ar[$i]["name"]=$re["NAME"];
	$ar[$i]["roll"]=$re["ROLLNO"];
	$i++;
}
echo json_encode($ar);
?>