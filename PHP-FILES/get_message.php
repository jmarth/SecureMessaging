

<?php

include_once 'connection.php';
	
	class Messages {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function get_messages($username)
		{
			$query = "Select * from messages where recipient='$username'";
			$res = mysqli_query($this->connection, $query);

			$result = array();
 
			while($row = mysqli_fetch_array($res)){
				array_push($result,
				array('id'=>$row[0],
				'message'=>$row[2],
				'timeout'=>$row[3],
				'timeoutDateTime'=>$row[4]
				));
			}

			echo json_encode(array("result"=>$result));
			mysqli_close($this->connection);
		}
		
	}
	
	
	$m = new Messages();

	if(isset($_POST['username'])) {
		$username = $_POST['username'];
		
		if(!empty($username)){
			$m->get_messages($username);	
		}else{
			echo json_encode("username is missing");
		}
		
	}


?>