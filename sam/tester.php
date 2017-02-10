<?php
require_once "conc.php";
$branch=["CSE","EEE","EE","CE","ME","IT"];
foreach ($branch as $table) {
$tablename=$table."_ATTENDANCE";
echo $tablename;
$sql="CREATE TABLE IF NOT EXISTS ".$tablename." (DATE VARCHAR(12) NOT NULL,PERIOD INT NOT NULL,YEARIN INT NOT NULL,SEMESTER VARCHAR(4) NOT NULL,DIVISION VARCHAR(2) NOT NULL,SUBJECTCODE VARCHAR(20) NOT NULL,ABSENT TEXT NULL,USERNAME VARCHAR(30) NOT NULL)" or die("can't create");
$res=$conn->query($sql);
if($res===true)
{
	echo "inserted";
}
else
{
	echo "already exist";
}
}
?>