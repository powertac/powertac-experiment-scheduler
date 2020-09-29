FROM frolvlad/alpine-python3

WORKDIR /powertac-agent

COPY broker.json .
COPY bunnie18.sh .

ENTRYPOINT ["/powertac-agent/bunnie18.sh"]

RUN echo "@testing http://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories \
    && apk add --update --no-cache openjdk8 bash python3-dev g++ py3-numpy py3-scipy py3-h5py@testing py3-grpcio@testing \
    && pip3 install virtualenv

RUN wget http://www.powertac.org/wiki/images/c/c9/Bunnie_2018.zip \
    && unzip Bunnie_2018.zip -d . bunnie\ preparation\ to\ upload/bunnielearner/* \
    && unzip Bunnie_2018.zip -d . bunnie\ preparation\ to\ upload/sample-broker-1.4.3.jar \
    && mv bunnie\ preparation\ to\ upload/* . \
    && rm Bunnie_2018.zip \
    && rm -rf bunnie\ preparation\ to\ upload \
    && cd bunnielearner \
    #&& pip3 install Cython --install-option="--no-cython-compile" \
    && pip3 install --no-cache-dir --upgrade https://storage.googleapis.com/tensorflow/mac/cpu/tensorflow-1.3.0-py3-none-any.whl \
    && pip3 install --no-cache-dir --prefer-binary --upgrade grpcio-tools \
    && pip3 install --no-cache-dir --prefer-binary --upgrade Keras

ENV BROKER_JAR_PATH=/powertac-agent/sample-broker-1.4.3.jar