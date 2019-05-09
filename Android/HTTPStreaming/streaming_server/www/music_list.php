<?php

$link = mysqli_connect("172.19.0.2", "root", "kth", "Streaming");

if(!$link)  
{
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();
}

mysqli_set_charset($link, "utf8");

$sql = "select * from music_list";
$result = mysqli_query($link, $sql);
$data = array();

if($result)
{
    while($row = mysqli_fetch_array($result))
    {
        array_push($data, 
            array('music_title'=>$row[0], 'music_artist'=>$row[1], 'music_owner'=>$row[2]));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("music_list"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
}
else
{  
    echo "SQL문 처리중 에러 발생 : "; 
    echo mysqli_error($link);
} 

mysqli_close($link);  
   
?>
