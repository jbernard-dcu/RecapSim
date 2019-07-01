protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python Utilisation.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python ApplicationModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python ExperimentModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python InfrastructureModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python WorkloadModel.proto
protoc-3.6.1-win32\bin\protoc.exe --proto_path=%cd% --python_out=python LocationModel.proto

cmd /k