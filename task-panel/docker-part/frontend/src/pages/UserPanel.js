
import { useState, useEffect } from "react";
import TaskItem from "./TaskItem";
import { useKeycloak } from "@react-keycloak/web";
import "./UserPanel.css";


const ToDoItems = () => {
  const [assignedTasks, setAssignedTasks] = useState([]);
  const [createdTasks, setCreatedTasks] = useState([]);
  const [token, setToken] = useState("");
  const { keycloak } = useKeycloak();

  const isAdmin = keycloak.tokenParsed?.realm_access?.roles.includes("admin");

  useEffect(() => {
    if (keycloak.authenticated) {
      setToken(keycloak.token);

      keycloak.updateToken(60).then((refreshed) => {
        if (refreshed) {
          setToken(keycloak.token);
        }
      });
    }
  }, [keycloak]);

  useEffect(() => {
    if (keycloak.authenticated) {
      fetchTasks(keycloak.token);
    }
  }, [keycloak.authenticated, keycloak.token]);

  const fetchTasks = async (authToken) => {
    try {
      // const assignedResponse = await fetch("http://localhost:3001/api/task", {
        // const assignedResponse = await fetch("/api-ex/task", {
          const assignedResponse = await fetch("/api/task", {
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      });

      if (!assignedResponse.ok) {
        throw new Error(`Assigned tasks error: ${assignedResponse.status}`);
      }

      const assignedData = await assignedResponse.json();
      setAssignedTasks(assignedData.tasks || []);

      if (isAdmin) {
        // const createdResponse = await fetch("http://localhost:3001/api/admin/tasks", {
          const createdResponse = await fetch("/api/admin/tasks", {
          headers: {
            Authorization: `Bearer ${authToken}`,
          },
        });

        if (!createdResponse.ok) {
          throw new Error(`Created tasks error: ${createdResponse.status}`);
        }

        const createdData = await createdResponse.json();
        setCreatedTasks(createdData.tasks || []);
      }
    } catch (error) {
      console.error("Error fetching tasks:", error);
    }
  };

  const handleAddTask = async (taskText, taskDate) => {
    try {
      // const response = await fetch("http://localhost:3001/api/task", {
        const response = await fetch("/api/task", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ title: taskText, date: taskDate }),
      });

      if (response.ok) {
        fetchTasks(token);
      } else {
        console.error("Error adding task:", response.statusText);
      }
    } catch (error) {
      console.error("Error adding task:", error);
    }
  };

  const handleDeleteTask = async (taskId) => {
    try {
      // const response = await fetch("http://localhost:3001/api/task", {
        const response = await fetch("/api/task", {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ task_id: taskId }),
      });

      if (response.ok) {
        fetchTasks(token);
      } else {
        console.error("Error deleting task:", response.statusText);
      }
    } catch (error) {
      console.error("Error deleting task:", error);
    }
  };

  const handleCompleteTask = async (taskId, completed) => {
    try {
      // const response = await fetch("http://localhost:3001/api/task", {
        // const response = await fetch("/api-ex/task", {
        const response = await fetch("/api/task", {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ task_id: taskId, completed }),  // Użyj parametru
      });
  
      if (response.ok) {
        fetchTasks(token);
      } else {
        console.error("Error updating task status:", response.statusText);
      }
    } catch (error) {
      console.error("Error updating task status:", error);
    }
  };
  

  return (
    <div className="to-do-container">
      <h1>All Tasks</h1>

      <h2>Your Assigned Tasks</h2>
      <ul>
        {assignedTasks.length > 0 ? (
          assignedTasks.map((task, i) => (
            <TaskItem
              key={`assigned-${i}`}
              task={task}
              currentUsername={keycloak.tokenParsed?.preferred_username}
              onComplete={() => handleCompleteTask(task.id, !task.completed)}
              onDelete={handleDeleteTask}
            />
          ))
        ) : (
          <li>No assigned tasks</li>
        )}
      </ul>

      {isAdmin && createdTasks.length > 0 && (
        <>
          <h2>Tasks You Created</h2>
          <ul>
            {createdTasks.map((entry, i) => (
  <li key={`created-${i}`}>
      <div>
        <strong>{entry.task.title}</strong> (Due: {entry.task.date})
        <ul>
          {entry.statuses.length > 0 ? (
            entry.statuses.map((s, j) => (
              <li key={j}>
                {s.username}:{" "}
                <span className={s.completed ? "status-done" : "status-not-done"}>
                  {s.completed ? "✔️ Done" : "❌ Not done"}
                </span>
              </li>
            ))
          ) : (
            <li>No assigned users</li>
          )}
        </ul>
      </div>
      <button
        className="delete"
        onClick={() => handleDeleteTask(entry.task.id)}
        style={{ marginLeft: "1rem" }}
      >
        🗑️ Delete
      </button>
  </li>
))}

          </ul>
        </>
      )}
    </div>
  );
};

export default ToDoItems;
