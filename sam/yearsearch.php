<?php
function yearsearch($subject)
{
	require "conc.php";
	$sql="select YEARIN from subject where SUBJECTCODE='$subject' LIMIT 1";
	$res=$conn->query($sql);
	while($row=$res->fetch_assoc())
	{
		return $row["YEARIN"];
	}
}
function branchsearch($subject)
{
	require "conc.php";
	$sql="select BRANCH from subject where SUBJECTCODE='$subject' LIMIT 1";
	$res=$conn->query($sql);
	while($row=$res->fetch_assoc())
	{
		return $row["BRANCH"];
	}
}
function semestersearch($subject)
{
	require "conc.php";
	$sql="select SEMESTER from subject where SUBJECTCODE='$subject' LIMIT 1";
	$res=$conn->query($sql);
	while($row=$res->fetch_assoc())
	{
		return $row["SEMESTER"];
	}
}
?>