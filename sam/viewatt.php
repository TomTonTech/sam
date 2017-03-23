
<?php
require_once "conc.php";
$sub=/*"cs2k602";//*/$_POST["subject"];
$div=/*"B";//*/strtoupper($_POST["division"]);
require_once "yearsearch.php";
$year=yearsearch($sub);
$branch=branchsearch($sub);
$sem=semestersearch($sub);
$array=array();
$ar=array();
$yr=$year;
$atttable=$branch."_attendance";
$stutable=$branch."_student";
$sql="select * from ".$stutable." where DIVISION='$div' AND YEAR='$year' order by ROLLNO asc;";
$res=$conn->query($sql);
while($row=$res->fetch_assoc())
{
	$rol=$row["ROLLNO"];
	$divs=$row["DIVISION"];
	$name=$row["NAME"];
	if($rol<10)
	{
		$rol="0".$rol;
	}
	$sql1="select count(*) as total from ".$atttable." where SUBJECTCODE='".$sub."' and YEARIN='".$year."' and DIVISION='".$divs."' and ABSENT like '%$rol%';";
	$res1=$conn->query($sql1);
	if($row1=$res1->fetch_assoc())
	{
		$total=$row1["total"];
	}
	$arr["students"][] = array(
		'name'=>$name,
		'rollno'=>$rol,
		'absent'=>$total
	);
}
$sql2="select count(*) as total from ".$atttable." where SUBJECTCODE='$sub' and DIVISION='$div' and YEARIN='$year';";
$res2=$conn->query($sql2);
if($row2=$res2->fetch_assoc())
{
	$arr["teacher"] =array("total"=>$row2["total"]);
}
echo json_encode($arr);
?>