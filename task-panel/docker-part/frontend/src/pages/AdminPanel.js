import React, { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import axios from "axios";
import './AdminPanel.css'
import AdminRoute from '../helpers/AdminRoute'


const TaskCreationForm = () => {
  const { keycloak } = useKeycloak();

  const [title, setTitle] = useState("");
  const [date, setDate] = useState("");
  const [availableEmployees, setAvailableEmployees] = useState([]);
  const [selectedEmployees, setSelectedEmployees] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (keycloak.authenticated) {
      axios
        // .get("http://localhost:3001/api/employees", {
        .get("/api/employees", {
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
          },
        })
        .then((response) => {
          setAvailableEmployees(response.data.employees);
        })
        .catch((error) => {
          console.error("Error fetching employees:", error);
        });
    }
  }, [keycloak.authenticated, keycloak.token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const result = await axios.post(
        // "http://localhost:3001/api/task/assign",
        "api/task/assign",
        {
          title,
          date,
          employees: selectedEmployees,
        },
        {
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
          },
        }
      );

      console.log("Task created successfully:", result.data);
      alert("Task successfully created!");
    } catch (error) {
      console.error("Error creating task:", error);
      alert("Error creating task!");
    } finally {
      setLoading(false);
    }
  };

  const handleEmployeeSelect = (email) => {
    setSelectedEmployees((prevSelected) => {
      if (prevSelected.includes(email)) {
        return prevSelected.filter((e) => e !== email);
      }
      return [...prevSelected, email];
    });
  };

  const today = new Date().toISOString().split("T")[0];


  return (
    <div>
      <h3>Create New Task</h3>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Task Title</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Task Date</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            required
            min={today}
          />
        </div>
        <div>
          <label>Select Employees</label>
          <div>
            {availableEmployees.length === 0 ? (
              // <p>Loading employees...</p>
              <p>0</p>
            ) : (
              availableEmployees.map((employee) => (
                <div key={employee.email}>
                  <input
                    type="checkbox"
                    id={employee.email}
                    checked={selectedEmployees.includes(employee.email)}
                    onChange={() => handleEmployeeSelect(employee.email)}
                  />
                  <label htmlFor={employee.email}>{employee.email}</label>
                </div>
              ))
            )}
          </div>
        </div>
        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create Task"}
        </button>
      </form>
    </div>
  );
};

const AdminPanel = () => {
  const { keycloak } = useKeycloak();

  useEffect(() => {
    console.log('[AdminPanel] Component mounted');
    console.log('[AdminPanel] User authenticated:', keycloak.authenticated);
    
    if (keycloak.authenticated) {
      console.log('[AdminPanel] User roles:', keycloak.realmAccess?.roles);
      console.log('[AdminPanel] Has admin role:', keycloak.hasResourceRole('admin'));
    }
  }, [keycloak, keycloak.authenticated]);

  return (
    <div className="admin-panel">
      <h2>Admin Panel</h2>
      <AdminRoute>
        <TaskCreationForm />
      </AdminRoute>
    </div>

  );
};

export default AdminPanel;
