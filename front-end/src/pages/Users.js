import { useEffect, useState } from "react";
import axios from "axios";

function Users() {

  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios.get("http://localhost:8080/users")
      .then(res => setUsers(res.data));
  }, []);

  return (
    <div>
      <h1>Users</h1>

      {users.map(user => (
        <div key={user.id}>
          {user.id} - {user.name}
        </div>
      ))}

    </div>
  );
}

export default Users;