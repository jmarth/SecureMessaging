
<?php

include_once 'connection.php';
	
	class Message {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
			mysqli_query($this->connection,"SET NAMES utf8");
		}
		
		public function send_message($recipient,$message,$timeout,$timeoutDateTime)
		{

			$this->connection->set_charset('utf8');
			mysqli_query($this->connection, 'SET NAMES utf8');

			$query = "Select * from users where user='$recipient'";
			$result = mysqli_query($this->connection, $query);
			$res = array();


			if(mysqli_num_rows($result)>0){
				$query = "INSERT INTO `messages` (`recipient`, `message`, `timeout`, `timeoutDateTime`) VALUES ( '$recipient','$message', '$timeout', '$timeoutDateTime')";
				$inserted = mysqli_query($this->connection, $query);
				if($inserted == 1){
					$status = "success";
				}else {
					$status = "write error";
				}
			}else {
				$status = "user ".$recipient." does not exist";
			}
			
			array_push($res,
					array('status'=>$status));
			echo json_encode(array("result"=>$res));
			mysqli_close($this->connection);
		}
		
	}
	
	
	$m = new Message();
	if(isset($_POST['recipient'],$_POST['message'],$_POST['timeout'], $_POST['timeoutDateTime'])) {
		$recipient = $_POST['recipient'];
		$message = $_POST['message'];
		$timeout = $_POST['timeout'];
		$timeoutDateTime = $_POST['timeoutDateTime'];
		
		if(!empty($recipient) && !empty($message)){
			$m->send_message($recipient,$message,$timeout,$timeoutDateTime);	
		}else{
			echo json_encode("some fileds are missing");
		}
		
	}

?>