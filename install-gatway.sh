export NGF_VERSION="2.8.0"

# 1. CRDs de la Gateway API (fournies par le dépôt NGF, à la version qu'il supporte)
kubectl kustomize "https://github.com/nginx/nginx-gateway-fabric/config/crd/gateway-api/standard?ref=v${NGF_VERSION}" | kubectl apply --server-side -f -

# 2. CRDs propres à NGF
kubectl apply --server-side -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v${NGF_VERSION}/deploy/crds.yaml

# 3. Le contrôleur
helm install ngf oci://ghcr.io/nginx/charts/nginx-gateway-fabric \
  --create-namespace -n nginx-gateway --version ${NGF_VERSION}


# 4. cert-manager
helm install cert-manager oci://quay.io/jetstack/charts/cert-manager \
  --version v1.21.2 -n cert-manager --create-namespace --set crds.enabled=true

# puis créer un ClusterIssuer self-signed pour générer des certificats TLS auto-signés pour le gateway api
# kubectl apply -f deploy/k8s/platform/cert-manager/clusterissuer-selfsigned.yaml

# installer ArgoCD pour gérer les déploiements de l'application
kubectl create namespace argocd
kubectl apply -n argocd --server-side --force-conflicts \
  -f https://raw.githubusercontent.com/argoproj/argo-cd/v3.2.3/manifests/install.yaml

## kubectl port-forward -n argocd svc/argocd-server 9443:443 &
## kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
# https://localhost:9443   utilisateur : admin
## kubectl get pods -n argocd -w

#3 pour tester le gateway api

#kubectl get pods -n nginx-gateway
#kubectl get gatewayclass          # "nginx" doit afficher ACCEPTED = True
#kubectl get pods -n cert-manager