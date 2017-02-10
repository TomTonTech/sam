<?php
$name=/*"cse03";//*/$_POST["username"];
require_once "conc.php";
$sql="select SUBJECT,DESIGNATION,BRANCH from login_credential where USERNAME='$name'";
$res=$conn->query($sql);
$ar=array();
while($re=$res->fetch_assoc())
{
	if(strpos($re["DESIGNATION"],"TUTOR")!==false)//the user is a tutor
	{
		$yr=explode(":",$re["DESIGNATION"])[1];
		$branch=$re["BRANCH"];
		$sql1="select SUBJECTCODE from subject where YEARIN='$yr' AND BRANCH='$branch'";
		$res1=$conn->query($sql1);
		$subject="";
		while($row1=$res1->fetch_assoc())
		{
			$subject.=$row1["SUBJECTCODE"].",";
		}
		$ar[]["subject"]=substr($subject,0,-1);
	}
	else if(strpos($re["DESIGNATION"],"HOD")!==false)//user is a hod
	{
		$branch=$re["BRANCH"];
		$sql1="select SUBJECTCODE from subject where BRANCH='$branch'";
		$res1=$conn->query($sql1);
		$subject="";
		while($row1=$res1->fetch_assoc())
		{
			$subject.=$row1["SUBJECTCODE"].",";
		}
		$ar[]["subject"]=substr($subject,0,-1);
	}
	else if(strpos($re["DESIGNATION"],"TEACHER")!==false)//the user is a regular teacher
	{
		$ar[]["subject"]=$re["SUBJECT"];
	}
}
echo json_encode($ar);
?>