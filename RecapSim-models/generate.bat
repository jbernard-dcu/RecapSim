protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java Utilisation.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java ApplicationModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java ExperimentModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java InfrastructureModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java WorkloadModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --java_out=src\main\java LocationModel.proto

cmd /k