<?php
require_once "conc.php";
require_once "msg.php";
require 'yearsearch.php';
$flag=1;
$division=/*"B";//*/strtoupper($_POST["division"]);
$period=/*"3";//*/$_POST["period"];
$subcode=/*"cs2k603";//*/$_POST['subcode'];
$username=/*"cse03";//*/$_POST['username'];
$username=strtolower($username);
$errormsg="";
$arr=array();
$yr=yearsearch($subcode);
$sem=semestersearch($subcode);
$branch=strtolower(branchsearch($subcode));
$atttable=$branch."_attendance";//table for attendence;
$stutable=$branch."_student";//student details table;
$stat=/*"02,03,04,10,42";//*/$_POST['status'];//students who are absent;
$date=date("d/m/y");
$sql2="select * from ".$atttable." where DATE='".$date."' and DIVISION='".$division."' and PERIOD='".$period."'";
$res2=$conn->query($sql2);
$whilflag=0;
while($row2=$res2->fetch_assoc())
{
	if($row2["USERNAME"]==$username)
	{
		$errormsg.="You Have Already Inserted The Information.\n";
		$flag=0;
	}
	if($whilflag==0 && $flag==1)
	{
		$errormsg.="There Is Another Row With Same Information Inserted By ".$row2["USERNAME"]."\n";
		$arr[] = array('extra' => $errormsg);
		$whilflag=1;
		//$errormsg.=sendMsg($username,$errormsg);
	}
}
if($flag==1)
{
	$sql = "insert into ".$atttable." set SUBJECTCODE='".$subcode."',PERIOD='".$period."',DATE='".$date."',DIVISION='".$division."',ABSENT='".$stat."',USERNAME='".$username."',YEARIN='".$yr."',SEMESTER='".$sem."'" or die("can't update");
	$res=$conn->query($sql);
	if ($res === TRUE) {
		//require_once "msg.php";
		$absentees=explode(",",$stat);
		foreach($absentees as $att)
		{
			$sql1="select NAME,PARENTNAME,PARENTNUM from ".$stutable." where ROLLNO='$att' AND YEAR='$yr' AND DIVISION='$division'";
			$res1=$conn->query($sql1);
			while($row3=$res1->fetch_assoc())
			{
				$mobile=$row3["PARENTNUM"];
				$name=$row3["NAME"];
				$parentname=$row3["PARENTNAME"];
				//smsgateway($mobile,$name,$parentname,$date,$period);
			}
		}
		$arr[]=array("message"=>"success");
	} 
	else
	{
    	$arr[]=array("message"=>"there are some errors");
	}	
}
else
{
	$arr[]=array("message"=>"You Have Already Inserted The Information.Try Again With Different Combination");
}
echo json_encode($arr);

?>