import React from "react";

const TaskItem = ({ task, currentUsername, onComplete, onDelete }) => {
  console.log(task)
  return (
    <li>
      <h3>{task.title}</h3>
      <p><strong>Due:</strong> {task.date}</p>
      <p><strong>Completed:</strong> {task.completed ? "Yes" : "No"}</p>
      <p><strong>Created by (admin):</strong> {task.admin}</p>

      {currentUsername === task.admin && (
        <>
          <button onClick={() => onDelete(task.id)}>Delete</button>
        </>
      )}
      <button onClick={() => onComplete(task.id)}>Mark as Completed</button>

    </li>
  );
};

export default TaskItem;

