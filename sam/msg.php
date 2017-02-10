<?php
function smsgateway($mobile,$student_name,$parent_name,$date,$period)

{// Replace with your username
$user = "souban354";

// Replace with your API KEY (We have sent API KEY on activation email, also available on panel)
$apikey = "NEcA3Wh68oyDGWkglzEv"; 

// Replace if you have your own Sender ID, else donot change
$senderid  =  "MYTEXT"; 

// Replace with the destination mobile Number to which you want to send sms 

// Replace with your Message content
$message   =  "Dear $parent_name,\nYour child $student_name was not present in the class on $date, $period period"; 
$message = urlencode($message);
// For Plain Text, use "txt" ; for Unicode symbols or regional Languages like hindi/tamil/kannada use "uni"
$type   =  "txt";

$ch = curl_init("http://smshorizon.co.in/api/sendsms.php?user=".$user."&apikey=".$apikey."&mobile=".$mobile."&senderid=".$senderid."&message=".$message."&type=".$type.""); 
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    $output = curl_exec($ch);      
    curl_close($ch); 

// Display MSGID of the successful sms push
echo $output;
}

?>