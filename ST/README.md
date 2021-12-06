# Comandos Aceites:
(recebe sempre uma palavra passe e uma hash, verifica se a hash foi assinada com a palavra passe e chave privada de SI, garantido assim não repudio, a chave publica do SI está num ficheiro chamado si_publickey.txt)

register_tcp_service palavra_passe hash_autenticacao descricao|ip|porta|register_key(nao sei bem o que é isso, implementado como string)

register_rmi_service palavra_passe hash_autenticacao descricao|ip|porta|name|register_key(nao sei bem o que é isso, implementado como string)

query_tcp palavra_passe hash_autenticacao (so verifica a autenticacao mas não devolve nenhum serviço)

query_rmi palavra_passe hash_autenticacao (so verifica a autenticacao mas não devolve nenhum serviço)

# Para correr
Executar run_st.bat no terminal ou abrir isto no intelij e fazer run ou abrir o ficheiro .bat no bloco de notas e ver os comandos para correr isto no terminal.