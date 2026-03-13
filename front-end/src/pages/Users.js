import { useEffect, useState } from "react";
import axios from "axios";

function Users() {

  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios.get("https://employee-management-ci-cd.onrender.com/users")
      .then(res => setUsers(res.data))
      .catch(err => console.error(err));
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