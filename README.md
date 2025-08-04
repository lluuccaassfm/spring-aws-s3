# Aplicação de Gerenciamento de Arquivos com AWS S3

Esta é uma aplicação Spring Boot que permite o upload e download de arquivos para buckets AWS S3, juntamente com a geração de URLs pré-assinadas.  A aplicação foi desenvolvida utilizando Java 21 e Spring Cloud AWS e pode ser testada localmente com o LocalStack.

## Tecnologias Utilizadas

*   **Java:** 21
*   **Spring Boot:** Framework para construção de aplicações Java.
*   **Spring Web:** Módulo do Spring para desenvolvimento de aplicações web.
*   **Spring Cloud AWS:** Integração com serviços da AWS, incluindo S3.
*   **LocalStack:** Plataforma para simular serviços da AWS localmente (para testes).

## Endpoints

### Upload de Arquivo

*   **Endpoint:** `POST /s3/upload/{bucket}`
*   **Descrição:** Faz o upload de um arquivo para um bucket S3 especificado.
*   **Parâmetros:**
    *   `bucket` (Path Variable): Nome do bucket S3.
    *   `file` (Request Parameter): O arquivo a ser enviado (MultipartFile).
*   **Exemplo de Requisição (cURL):**

    ```bash
    curl -X POST -F "file=@/caminho/do/seu/arquivo.txt" http://localhost:8080/upload/seu-bucket
    ```

*   **Respostas:**
    *   **200 OK:** "File uploaded successfully to bucket: {bucket}"
    *   **500 Internal Server Error:** "File upload failed"

### Download de Arquivo

*   **Endpoint:** `GET /s3/download/{bucket}/{key}`
*   **Descrição:** Faz o download de um arquivo de um bucket S3 especificado.
*   **Parâmetros:**
    *   `bucket` (Path Variable): Nome do bucket S3.
    *   `key` (Path Variable): Nome do arquivo no bucket.
*   **Exemplo de Requisição (cURL):**

    ```bash
    curl http://localhost:8080/download/seu-bucket/nome-do-arquivo.txt > arquivo_baixado.txt
    ```

*   **Respostas:**
    *   **200 OK:** Arquivo em formato de array de bytes no corpo da resposta.
    *   **500 Internal Server Error:**  Corpo da resposta vazio.

### Geração de URL Pré-assinada

*   **Endpoint:** `GET /s3/generate-presigned-url`
*   **Descrição:** Gera uma URL pré-assinada para acessar um arquivo no S3, permitindo acesso temporário sem credenciais AWS.
*   **Parâmetros:**
    *   `bucketName` (Request Parameter): Nome do bucket S3.
    *   `fileName` (Request Parameter): Nome do arquivo no bucket.
    *   `durationMinutes` (Request Parameter, Opcional): Duração da validade da URL em minutos (padrão: 5).
*   **Exemplo de Requisição (cURL):**

    ```bash
    curl "http://localhost:8080/generate-presigned-url?bucketName=seu-bucket&fileName=nome-do-arquivo.txt&durationMinutes=10"
    ```

*   **Respostas:**
    *   **200 OK:** URL pré-assinada no corpo da resposta.

## Testando com LocalStack

Para testar a aplicação localmente, você pode utilizar o LocalStack.  Certifique-se de ter o Docker instalado.

1.  **Inicie o LocalStack:**

    ```bash
    docker-compose up
    ```

2.  **Configure as variáveis de ambiente ou propriedades da aplicação** para apontar para o LocalStack (endpoint, region, etc.).

3.  **Crie um bucket no LocalStack:**

    ```bash
    aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket seu-bucket --region us-east-1
    ```

4.  **Execute a aplicação Spring Boot.**

5.  **Utilize os endpoints da aplicação para fazer upload, download e gerar URLs pré-assinadas.**

## Considerações

*   Certifique-se de ter as credenciais AWS configuradas corretamente (mesmo para o LocalStack, você precisa de credenciais mockadas).
*   Adapte os exemplos de requisição cURL com os seus valores de bucket e nome de arquivo.
*   A configuração do LocalStack pode variar dependendo da sua necessidade.  Consulte a documentação do LocalStack para mais detalhes.