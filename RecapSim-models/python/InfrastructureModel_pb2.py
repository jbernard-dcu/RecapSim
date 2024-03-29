# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: InfrastructureModel.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


import LocationModel_pb2 as LocationModel__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='InfrastructureModel.proto',
  package='models',
  syntax='proto3',
  serialized_options=_b('\n\023eu.recap.sim.modelsB\023InfrastructureModel'),
  serialized_pb=_b('\n\x19InfrastructureModel.proto\x12\x06models\x1a\x13LocationModel.proto\"`\n\x0eInfrastructure\x12\x0c\n\x04name\x18\x03 \x01(\t\x12\x1b\n\x05links\x18\x01 \x03(\x0b\x32\x0c.models.Link\x12#\n\x05sites\x18\x02 \x03(\x0b\x32\x14.models.ResourceSite\"\xd8\x01\n\x0cResourceSite\x12\x0c\n\x04name\x18\x01 \x01(\t\x12\n\n\x02id\x18\x02 \x01(\t\x12\"\n\x08location\x18\x03 \x01(\x0b\x32\x10.models.Location\x12\x1b\n\x05nodes\x18\x04 \x03(\x0b\x32\x0c.models.Node\x12\x36\n\x0ehierarchyLevel\x18\x05 \x01(\x0e\x32\x1e.models.ResourceSite.SiteLevel\"5\n\tSiteLevel\x12\x08\n\x04\x45\x64ge\x10\x00\x12\x08\n\x04\x43ore\x10\x01\x12\t\n\x05Metro\x10\x02\x12\t\n\x05\x43loud\x10\x03\"\xf9\x02\n\x04Node\x12\x0c\n\x04name\x18\x02 \x01(\t\x12\n\n\x02id\x18\x03 \x01(\t\x12)\n\x0fprocessingUnits\x18\x04 \x03(\x0b\x32\x10.models.Node.CPU\x12(\n\x0bmemoryUnits\x18\x05 \x03(\x0b\x32\x13.models.Node.Memory\x12*\n\x0cstorageUnits\x18\x06 \x03(\x0b\x32\x14.models.Node.Storage\x1au\n\x03\x43PU\x12\n\n\x02id\x18\x04 \x01(\t\x12\x0c\n\x04name\x18\x02 \x01(\t\x12\x0c\n\x04make\x18\x03 \x01(\t\x12\x11\n\tfrequency\x18\x06 \x01(\x05\x12#\n\x08\x63puCores\x18\x01 \x03(\x0b\x32\x11.models.Node.Core\x12\x0e\n\x06rating\x18\x05 \x01(\t\x1a\x12\n\x04\x43ore\x12\n\n\x02id\x18\x01 \x01(\t\x1a&\n\x06Memory\x12\n\n\x02id\x18\x01 \x01(\t\x12\x10\n\x08\x63\x61pacity\x18\x02 \x01(\x05\x1a#\n\x07Storage\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0c\n\x04size\x18\x02 \x01(\x05\"R\n\x04Link\x12\n\n\x02id\x18\x02 \x01(\t\x12\x10\n\x08\x62\x61ndwith\x18\x01 \x01(\x05\x12,\n\x0e\x63onnectedSites\x18\x04 \x03(\x0b\x32\x14.models.ResourceSiteB*\n\x13\x65u.recap.sim.modelsB\x13InfrastructureModelb\x06proto3')
  ,
  dependencies=[LocationModel__pb2.DESCRIPTOR,])



_RESOURCESITE_SITELEVEL = _descriptor.EnumDescriptor(
  name='SiteLevel',
  full_name='models.ResourceSite.SiteLevel',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='Edge', index=0, number=0,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='Core', index=1, number=1,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='Metro', index=2, number=2,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='Cloud', index=3, number=3,
      serialized_options=None,
      type=None),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=320,
  serialized_end=373,
)
_sym_db.RegisterEnumDescriptor(_RESOURCESITE_SITELEVEL)


_INFRASTRUCTURE = _descriptor.Descriptor(
  name='Infrastructure',
  full_name='models.Infrastructure',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='name', full_name='models.Infrastructure.name', index=0,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='links', full_name='models.Infrastructure.links', index=1,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='sites', full_name='models.Infrastructure.sites', index=2,
      number=2, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=58,
  serialized_end=154,
)


_RESOURCESITE = _descriptor.Descriptor(
  name='ResourceSite',
  full_name='models.ResourceSite',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='name', full_name='models.ResourceSite.name', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='id', full_name='models.ResourceSite.id', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='location', full_name='models.ResourceSite.location', index=2,
      number=3, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='nodes', full_name='models.ResourceSite.nodes', index=3,
      number=4, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='hierarchyLevel', full_name='models.ResourceSite.hierarchyLevel', index=4,
      number=5, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
    _RESOURCESITE_SITELEVEL,
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=157,
  serialized_end=373,
)


_NODE_CPU = _descriptor.Descriptor(
  name='CPU',
  full_name='models.Node.CPU',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Node.CPU.id', index=0,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='name', full_name='models.Node.CPU.name', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='make', full_name='models.Node.CPU.make', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='frequency', full_name='models.Node.CPU.frequency', index=3,
      number=6, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='cpuCores', full_name='models.Node.CPU.cpuCores', index=4,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='rating', full_name='models.Node.CPU.rating', index=5,
      number=5, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=539,
  serialized_end=656,
)

_NODE_CORE = _descriptor.Descriptor(
  name='Core',
  full_name='models.Node.Core',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Node.Core.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=658,
  serialized_end=676,
)

_NODE_MEMORY = _descriptor.Descriptor(
  name='Memory',
  full_name='models.Node.Memory',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Node.Memory.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='capacity', full_name='models.Node.Memory.capacity', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=678,
  serialized_end=716,
)

_NODE_STORAGE = _descriptor.Descriptor(
  name='Storage',
  full_name='models.Node.Storage',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Node.Storage.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='size', full_name='models.Node.Storage.size', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=718,
  serialized_end=753,
)

_NODE = _descriptor.Descriptor(
  name='Node',
  full_name='models.Node',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='name', full_name='models.Node.name', index=0,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Node.id', index=1,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='processingUnits', full_name='models.Node.processingUnits', index=2,
      number=4, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='memoryUnits', full_name='models.Node.memoryUnits', index=3,
      number=5, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='storageUnits', full_name='models.Node.storageUnits', index=4,
      number=6, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[_NODE_CPU, _NODE_CORE, _NODE_MEMORY, _NODE_STORAGE, ],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=376,
  serialized_end=753,
)


_LINK = _descriptor.Descriptor(
  name='Link',
  full_name='models.Link',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='models.Link.id', index=0,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='bandwith', full_name='models.Link.bandwith', index=1,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='connectedSites', full_name='models.Link.connectedSites', index=2,
      number=4, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=755,
  serialized_end=837,
)

_INFRASTRUCTURE.fields_by_name['links'].message_type = _LINK
_INFRASTRUCTURE.fields_by_name['sites'].message_type = _RESOURCESITE
_RESOURCESITE.fields_by_name['location'].message_type = LocationModel__pb2._LOCATION
_RESOURCESITE.fields_by_name['nodes'].message_type = _NODE
_RESOURCESITE.fields_by_name['hierarchyLevel'].enum_type = _RESOURCESITE_SITELEVEL
_RESOURCESITE_SITELEVEL.containing_type = _RESOURCESITE
_NODE_CPU.fields_by_name['cpuCores'].message_type = _NODE_CORE
_NODE_CPU.containing_type = _NODE
_NODE_CORE.containing_type = _NODE
_NODE_MEMORY.containing_type = _NODE
_NODE_STORAGE.containing_type = _NODE
_NODE.fields_by_name['processingUnits'].message_type = _NODE_CPU
_NODE.fields_by_name['memoryUnits'].message_type = _NODE_MEMORY
_NODE.fields_by_name['storageUnits'].message_type = _NODE_STORAGE
_LINK.fields_by_name['connectedSites'].message_type = _RESOURCESITE
DESCRIPTOR.message_types_by_name['Infrastructure'] = _INFRASTRUCTURE
DESCRIPTOR.message_types_by_name['ResourceSite'] = _RESOURCESITE
DESCRIPTOR.message_types_by_name['Node'] = _NODE
DESCRIPTOR.message_types_by_name['Link'] = _LINK
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Infrastructure = _reflection.GeneratedProtocolMessageType('Infrastructure', (_message.Message,), dict(
  DESCRIPTOR = _INFRASTRUCTURE,
  __module__ = 'InfrastructureModel_pb2'
  # @@protoc_insertion_point(class_scope:models.Infrastructure)
  ))
_sym_db.RegisterMessage(Infrastructure)

ResourceSite = _reflection.GeneratedProtocolMessageType('ResourceSite', (_message.Message,), dict(
  DESCRIPTOR = _RESOURCESITE,
  __module__ = 'InfrastructureModel_pb2'
  # @@protoc_insertion_point(class_scope:models.ResourceSite)
  ))
_sym_db.RegisterMessage(ResourceSite)

Node = _reflection.GeneratedProtocolMessageType('Node', (_message.Message,), dict(

  CPU = _reflection.GeneratedProtocolMessageType('CPU', (_message.Message,), dict(
    DESCRIPTOR = _NODE_CPU,
    __module__ = 'InfrastructureModel_pb2'
    # @@protoc_insertion_point(class_scope:models.Node.CPU)
    ))
  ,

  Core = _reflection.GeneratedProtocolMessageType('Core', (_message.Message,), dict(
    DESCRIPTOR = _NODE_CORE,
    __module__ = 'InfrastructureModel_pb2'
    # @@protoc_insertion_point(class_scope:models.Node.Core)
    ))
  ,

  Memory = _reflection.GeneratedProtocolMessageType('Memory', (_message.Message,), dict(
    DESCRIPTOR = _NODE_MEMORY,
    __module__ = 'InfrastructureModel_pb2'
    # @@protoc_insertion_point(class_scope:models.Node.Memory)
    ))
  ,

  Storage = _reflection.GeneratedProtocolMessageType('Storage', (_message.Message,), dict(
    DESCRIPTOR = _NODE_STORAGE,
    __module__ = 'InfrastructureModel_pb2'
    # @@protoc_insertion_point(class_scope:models.Node.Storage)
    ))
  ,
  DESCRIPTOR = _NODE,
  __module__ = 'InfrastructureModel_pb2'
  # @@protoc_insertion_point(class_scope:models.Node)
  ))
_sym_db.RegisterMessage(Node)
_sym_db.RegisterMessage(Node.CPU)
_sym_db.RegisterMessage(Node.Core)
_sym_db.RegisterMessage(Node.Memory)
_sym_db.RegisterMessage(Node.Storage)

Link = _reflection.GeneratedProtocolMessageType('Link', (_message.Message,), dict(
  DESCRIPTOR = _LINK,
  __module__ = 'InfrastructureModel_pb2'
  # @@protoc_insertion_point(class_scope:models.Link)
  ))
_sym_db.RegisterMessage(Link)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
