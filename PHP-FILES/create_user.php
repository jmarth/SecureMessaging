
<?php

include_once 'connection.php';
	
	class User {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function does_user_exist($username,$password,$encrypted_password)
		{
			$query = "Select * from users where user='$username'";
			$result = mysqli_query($this->connection, $query);

			$res = array();
			$status="";

			if(mysqli_num_rows($result)>0){
				$status = ' already user '.$username;
			}
			else{
				$query = "INSERT INTO `users` (`user`, `password`, `status`) VALUES ( '$username','$encrypted_password', 'u')";
				$inserted = mysqli_query($this->connection, $query);

				if($inserted == 1){
					$status = "success";
				}else {
					$status = "error on write";
				}
			}
			array_push($res,
					array('status'=>$status));
			echo json_encode(array("result"=>$res));
			mysqli_close($this->connection);
			
		}
		
	}
	
	
	$user = new User();
	if(isset($_POST['username'],$_POST['password'])) {
		$username = $_POST['username'];
		$password = $_POST['password'];
		
		if(!empty($username) && !empty($password)){
			
			$encrypted_password = md5($password);
			$user->does_user_exist($username, $password, $encrypted_password);
			
		}else{
			echo json_encode("you must type both inputs");
		}
		
	}

?>