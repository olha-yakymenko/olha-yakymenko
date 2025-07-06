# Task Management Application with Role-Based Access Control

This is a task management system featuring role-based access control, powered by **Keycloak** for secure authentication and authorization.

---

## Features

- Role-based access control for flexible user permissions  
- Secure authentication and authorization using Keycloak  
- Task creation, assignment, and tracking  
- RESTful backend architecture  
- Docker and Kubernetes deployment support

---

## Getting Started

### Running with Docker

1. Clone the repository and navigate to the Docker directory:

   ```bash
   git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
   cd docker-part
2. Start the application:

   ```bash
   docker-compose up

3. To run in detached mode:

   ```bash
    docker-compose up -d



### Running on Kubernetes

1. Clone the repository and navigate to the Kubernetes directory:

   ```bash
    git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
    cd kubernetes-part
2. Ensure the following components are installed in your Kubernetes cluster:

Ingress NGINX

Metrics Server

CoreDNS

Install missing components if necessary:

   ```bash
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
    kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.1/components.yaml

3. Verify CoreDNS is running:

   ```bash
  kubectl get pods -n kube-system -l k8s-app=coredns

4. Deploy the application manifests:

   ```bash
  kubectl apply -f .
Wait for all resources to start and stabilize.

