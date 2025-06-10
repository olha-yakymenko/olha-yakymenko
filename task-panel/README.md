Aplikacja do zarządzania zadaniami z kontrolą dostępu opartą na rolach, wykorzystującą Keycloak do uwierzytelniania i autoryzacji.
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

