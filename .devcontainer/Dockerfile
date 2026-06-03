FROM eclipse-temurin:21-jdk

ARG USERNAME=dev
ARG USER_UID=1000
ARG USER_GID=1000

RUN \
  # Handle group
  GROUP_NAME=$(getent group $USER_GID | cut -d: -f1 || true) && \
  if [ -z "$GROUP_NAME" ]; then \
    groupadd -g $USER_GID $USERNAME && GROUP_NAME=$USERNAME; \
  fi && \
  # Handle user: if UID already exists, reuse or rename that user
  EXISTING_USER=$(getent passwd $USER_UID | cut -d: -f1 || true) && \
  if [ -z "$EXISTING_USER" ]; then \
    useradd -m -u $USER_UID -g "$GROUP_NAME" -s /bin/bash $USERNAME; \
  elif [ "$EXISTING_USER" != "$USERNAME" ]; then \
    usermod -l $USERNAME -d /home/$USERNAME -m "$EXISTING_USER" && \
    usermod -g "$GROUP_NAME" $USERNAME; \
  fi && \
  echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

ENV HOME=/home/$USERNAME

RUN mkdir -p /workspace \
 && chown $USER_UID:$USER_GID /workspace

RUN apt-get update && apt-get install -y \
    git curl openssh-client build-essential procps lsof \
    wl-clipboard

ARG GIT_USER_EMAIL="default@example.com"
ARG GIT_USER_NAME="default_user"
RUN git config --global --add safe.directory /workspace \
 && git config --global user.name "${GIT_USER_NAME}" \
 && git config --global user.email "${GIT_USER_EMAIL}"

# Pre-crear directorios del home con permisos correctos ANTES de cambiar de usuario
RUN mkdir -p \
    /home/$USERNAME/.cache/nvim \
    /home/$USERNAME/.cache/pip \
    /home/$USERNAME/.local/share/nvim \
    /home/$USERNAME/.local/state/nvim \
    /home/$USERNAME/.local/bin \
    /home/$USERNAME/.config \
 && chown -R $USER_UID:$USER_GID /home/$USERNAME   # <-- cubre todo el home

RUN /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
ENV PATH="/home/linuxbrew/.linuxbrew/bin:/home/linuxbrew/.linuxbrew/sbin:${PATH}"
RUN brew install lazygit fzf neovim ripgrep fd maven
RUN brew install anomalyco/tap/opencode

USER $USERNAME

RUN git clone https://github.com/LazyVim/starter ~/.config/nvim
RUN nvim --headless "+Lazy! sync" +qa

WORKDIR /workspace
