# Task Management Application with Role-Based Access Control

This is a task management system featuring role-based access control, powered by **Keycloak** for secure authentication and authorization.

The application is designed with security in mind — all access is protected using **Keycloak**, an open-source identity and access management solution. Users must authenticate via Keycloak, and their access is restricted based on assigned roles. This ensures only authorized users can perform specific actions within the system (e.g., creating, assigning, or modifying tasks).

---

## Features

- 🔐 **Secure login and token-based authentication via Keycloak**
- 🛡️ **Role-based access control (RBAC)** for flexible and granular user permissions
- ✅ Task creation, assignment, and progress tracking
- 🌐 RESTful backend API
- 🐳 Docker support for containerized deployment
- ☸️ Kubernetes manifests for cloud-native deployment


---

## Getting Started

### Running with Docker

1. Clone the repository and navigate to the Docker directory:

   ```bash
   git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
   cd docker-part
   ```
2. Start the application:

   ```bash
   docker-compose up
   ```
3. To run in detached mode:

   ```bash
    docker-compose up -d
   ```


### Running on Kubernetes

1. Clone the repository and navigate to the Kubernetes directory:

   ```bash
    git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
    cd kubernetes-part
   ```
2. Ensure the following components are installed in your Kubernetes cluster:

Ingress NGINX

Metrics Server

CoreDNS

Install missing components if necessary:

   ```bash
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
    kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.1/components.yaml ```
   ```
3. Verify CoreDNS is running:

   ```bash
     kubectl get pods -n kube-system -l k8s-app=coredns   
   ```
4. Deploy the application manifests:

   ```bash
     kubectl apply -f .
   ```
Wait for all resources to start and stabilize.

---
Instrukcja uruchomienia projektu

1. Uruchomienie części Docker

Aby uruchomić aplikację w środowisku Docker, proszę wykonać poniższe kroki:

a) Pobranie kodu źródłowego
git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
cd docker-part

b) Uruchomienie aplikacji za pomocą Docker
Aby uruchomić aplikację, należy wydać polecenie:

docker-compose up
Aby uruchomić kontenery w tle (detached mode), można użyć flagi -d:

docker-compose up -d
Po wykonaniu powyższych kroków aplikacja powinna zostać uruchomiona w środowisku Docker.

2. Uruchomienie części Kubernetes

Aby uruchomić aplikację w Kubernetes, proszę wykonać następujące kroki:

a) Pobranie kodu źródłowego
git clone https://github.com/olha-yakymenko/technologie_chmurowe_projekt.git
cd kubernetes-part
b) Wymagania wstępne
Proszę upewnić się, że w klastrze Kubernetes są zainstalowane następujące komponenty:

Ingress NGINX – zarządza ruchem HTTP(S) do klastra.
Metrics Server – zbiera dane o zużyciu zasobów (CPU, pamięć) i umożliwia monitorowanie.
CoreDNS – zapewnia rozwiązywanie nazw DNS w klastrze.
c) Instalacja wymaganych komponentów
Ingress NGINX

Jeśli nie jest zainstalowany, można go zainstalować poleceniem:

kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
Metrics Server

Jeśli Metrics Server nie jest obecny, proszę zainstalować go poleceniem:

kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.6.1/components.yaml
Uwaga: Upewnij się, że używasz odpowiedniej wersji, jeśli jest to wymagane.
CoreDNS

CoreDNS jest zazwyczaj domyślnie zainstalowane w klastrze. Można to sprawdzić poleceniem:

kubectl get pods -n kube-system -l k8s-app=coredns
Jeśli CoreDNS nie jest zainstalowane, proszę zainstalować je zgodnie z dokumentacją Kubernetes.

d) Uruchomienie aplikacji
Aby zaaplikować konfiguracje z katalogu kubernetes-part, należy wydać polecenie:

kubectl apply -f .
Proszę poczekać, aż wszystkie zasoby zostaną poprawnie uruchomione. Czas oczekiwania zależy od zasobów systemowych i prędkości połączenia.

