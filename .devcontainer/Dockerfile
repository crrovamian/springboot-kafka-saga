FROM eclipse-temurin:21-jdk
RUN mkdir -p /app
RUN apt-get update && apt-get install -y git curl openssh-client build-essential procps lsof wl-clipboard
RUN git config --global --add safe.directory /app
ARG GIT_USER_EMAIL="default@example.com"
ARG GIT_USER_NAME="default_user" 
RUN git config --global user.name "${GIT_USER_NAME}"
RUN git config --global user.email "${GIT_USER_EMAIL}"
RUN /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
ENV PATH="/home/linuxbrew/.linuxbrew/bin:/home/linuxbrew/.linuxbrew/sbin:${PATH}"
RUN brew install lazygit fzf neovim ripgrep fd maven
RUN brew install anomalyco/tap/opencode
RUN git clone https://github.com/LazyVim/starter /root/.config/nvim
WORKDIR /app
