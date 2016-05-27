<?php

include_once 'connection.php';
	
	class Time {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function get_time()
		{
			$query = "SET time_zone = '-4:00';";
			$query1 = "SELECT NOW();";
			mysqli_query($this->connection, $query);
			$res = mysqli_query($this->connection, $query1);

			$result = array();
 
			while($row = mysqli_fetch_array($res)){
				array_push($result,
				array('time'=>$row[0]));
			}

			echo json_encode(array("result"=>$result));
			mysqli_close($this->connection);
		}
		
	}
	
	
	$t = new Time();
	$t->get_time();

?>