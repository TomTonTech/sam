<?php
$subject=/*"cs2k603";//*/$_POST["subject"];
$name=/*"ABDUL SHAMMAS P K";//*/$_POST["name"];
$div=/*"A";//*/strtoupper($_POST["division"]);
$ar = array();
require_once "conc.php";
require_once "yearsearch.php";
$yr=yearsearch($subject);
$branch=branchsearch($subject);
$tablename=$branch."_attendance";
$stutable=$branch."_student";
$sql1="select ROLLNO from ".$stutable." where NAME='$name' and DIVISION='$div' and YEAR='$yr'";
$res1=$conn->query($sql1);
if($row1=$res1->fetch_assoc())
{
	$roll=$row1["ROLLNO"];
	if($roll<10)
		$roll="0".$roll;
	$sql="select * from ".$tablename." where SUBJECTCODE='$subject' and DIVISION='$div' and YEARIN='$yr' order by DATE desc";
	$res=$conn->query($sql);
	while($row=$res->fetch_assoc())
	{
		$rollno=explode(",",$row["ABSENT"]);
		if(in_array($roll, $rollno))
		{
			$stat=0;
		}
		else
		{
			$stat=1;
		}
		$ar["student"][]=array(
			'date'=>$row["DATE"],
			'period'=>$row["PERIOD"],
			'status'=>$stat
			);
	}
}
else
{
	$ar["error"][]=array(
		'key'=>"cant find name");
}
echo json_encode($ar);
?>