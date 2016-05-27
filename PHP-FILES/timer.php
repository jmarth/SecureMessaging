<?php

include_once 'connection.php';
	
	class Timer {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_timer($id,$dateTime)
		{
			$query = "UPDATE `messages` SET  `timeoutDateTime` =  '$dateTime' WHERE id = '$id'";
			$updated = mysqli_query($this->connection, $query);

			$result = array();
			$status="";

			if($updated == 1){
				$status = "success";
			}else {
				$status = "error";
			}

			array_push($result,
					array('status'=>$status));
			echo json_encode(array("result"=>$result));
			mysqli_close($this->connection);
		}
		
	}
	
	
	$t = new Timer();

	if(isset($_POST['id'],$_POST['dateTime'])) {
		$id = $_POST['id'];
		$dateTime = $_POST['dateTime'];
		
		if(!empty($id) && !empty($dateTime)){
			$t->update_timer($id,$dateTime);	
		}else{
			echo json_encode("missing field");
		}
		
	}


?>