<?php

include_once 'connection.php';
	
	class User {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function does_user_exist($username,$password)
		{
			$query = "Select * from users where user='$username' and password = '$password' and status = 'u'";
			$resultUser = mysqli_query($this->connection, $query);
			$query = "Select * from users where user='$username' and password = '$password' and status = 'a' ";
			$resultAdmin = mysqli_query($this->connection, $query);
			$result = array();
			$status="";

			if(mysqli_num_rows($resultUser)>0){
				$status = "user";
			}else if(mysqli_num_rows($resultAdmin)>0) {
				$status = "admin";
			}
			else{
				$status = "error";
			}
			array_push($result,
					array('status'=>$status));
			echo json_encode(array("result"=>$result));
			mysqli_close($this -> connection);
		}
		
	}
	
	
	$user = new User();
	if(isset($_POST['username'],$_POST['password'])) {
		$username = $_POST['username'];
		$password = $_POST['password'];
		
		if(!empty($username) && !empty($password)){
			
			$encrypted_password = md5($password);
			$user->does_user_exist($username,$encrypted_password);
			
		}else{
			echo json_encode("you must type both inputs");
		}
		
	}

?>